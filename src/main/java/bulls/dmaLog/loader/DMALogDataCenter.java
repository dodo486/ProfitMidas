package bulls.dmaLog.loader;

import bulls.designTemplate.observer.ConcreteObserverStation;
import bulls.designTemplate.observer.Filter;
import bulls.designTemplate.observer.Observer;
import bulls.designTemplate.observer.ObserverStation;
import bulls.dmaLog.*;
import bulls.log.DefaultLogger;
import bulls.staticData.TempConf;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum DMALogDataCenter {
    Instance;

    private final ObserverStation<DMALogList> observerStation = new ConcreteObserverStation<>("FEPDataManager");
    private DMALogList lastData;
    private DataLoader<DMALogList> loader;
    private boolean pollingMode = false;

    // OrderId -> ConcurrentLinkedQueue<DMALog>
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<DMALog>> orderIdFullLogMap;
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<RequestDMALog>> orderIdRequestLogMap;
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<ReportDMALog>> orderIdReportLogMap;
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<TradeDMALog>> orderIdTradeLogMap;

    DMALogDataCenter() {
        orderIdFullLogMap = new ConcurrentHashMap<>();
        orderIdRequestLogMap = new ConcurrentHashMap<>();
        orderIdReportLogMap = new ConcurrentHashMap<>();
        orderIdTradeLogMap = new ConcurrentHashMap<>();
    }

    public void addObserver(String key, Observer<DMALogList> o) {
        addObserver(key, o, x -> true);
    }

    public void addObserver(String key, Observer<DMALogList> o, Filter<DMALogList> f) {
        observerStation.addObserver(key, o, f);
    }

    public void removeObserver(String key) {
        observerStation.deleteObserver(key);
    }

    public boolean getPollingMode() {
        return pollingMode;
    }

    public void setPollingMode(boolean pollingMode) {
        this.pollingMode = pollingMode;
    }

    public void setLoader(DataLoader<DMALogList> loader) {
        this.loader = loader;
    }

    public DataLoader<DMALogList> getLoader() {
        return loader;
    }

    public void setData(DMALogList data) {
        this.lastData = data;
    }

    public void setDataAndNotify(DMALogList data) {
        setData(data);

        if (data != null) {
            update(data);
            observerStation.notifyAll(data);
        }
    }

    public void loadData() {
        if (loader != null)
            this.lastData = loader.load();
    }

    private void internalLoadDataAndNotify() {
        loadData();

        if (lastData != null) {
            update(lastData);
            observerStation.notifyAll(lastData);
        }
    }

    public void loadDataAndNotify() {
        if (!pollingMode)
            internalLoadDataAndNotify();
    }

    public void loadDataAndNotifyForced() {
        internalLoadDataAndNotify();
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<DMALog>> getOrderIdFullLogMap() {
        return orderIdFullLogMap;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<RequestDMALog>> getOrderIdRequestLogMap() {
        return orderIdRequestLogMap;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<ReportDMALog>> getOrderIdReportLogMap() {
        return orderIdReportLogMap;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<TradeDMALog>> getOrderIdTradeLogMap() {
        return orderIdTradeLogMap;
    }

    public void update(DMALogList logListObj) {
        for (DMALog log : logListObj.getLogList()) {
            String orderId = log.getCurrentOrderId();
            orderIdFullLogMap.computeIfAbsent(orderId, k -> new ConcurrentLinkedQueue<>()).add(log);

            if (log instanceof RequestDMALog)
                orderIdRequestLogMap.computeIfAbsent(orderId, k -> new ConcurrentLinkedQueue<>()).add((RequestDMALog) log);
            else if (log instanceof ReportDMALog)
                orderIdReportLogMap.computeIfAbsent(orderId, k -> new ConcurrentLinkedQueue<>()).add((ReportDMALog) log);
            else if (log instanceof TradeDMALog)
                orderIdTradeLogMap.computeIfAbsent(orderId, k -> new ConcurrentLinkedQueue<>()).add((TradeDMALog) log);
        }

        System.out.println(logListObj.getLogList().size() + " Data Loading Complete");
    }

    public void start(int pollingSec) {
        boolean pollingMode = pollingSec > 0;

        LocalTime startTime = LocalTime.of(TempConf.MARKET_START_HOUR, TempConf.MARKET_START_MINUTE, 0).minusMinutes(15);
        LocalTime endTime = LocalTime.of(TempConf.MARKET_END_HOUR, TempConf.MARKET_END_MINUTE, 0).plusMinutes(30);

        DefaultLogger.logger.info("DMALogDataCenter 시작 : pollingMode={}, pollingSec={}, startTime={}, endTime={}", pollingMode, pollingSec, startTime, endTime);

        new Thread(new RefreshTask(startTime, endTime, pollingMode, pollingSec)).start();
    }

    // startTime ~ endTime 동안 period 분 간격으로 작동
    // pollingMode ON 상태이면 각종 함수에서 파일을 읽으라는 요청 (loadDataAndNotify)은
    // 모두 무시되고 여기서만 pollingSec 초 간격마다 파일을 읽게 됨 (loadDataAndNotifyForced)
    static class RefreshTask implements Runnable {
        private final LocalTime startTime;
        private final LocalTime endTime;
        private int period;
        private final DMALogDataCenter manager;
        private final int defaultWaitDelay = 5 * 60 * 1000; // 5 min

        @Override
        public void run() {
            LocalDate date = LocalDate.now();
            manager.loadDataAndNotifyForced();
            for (; ; ) {
                LocalDate currentDate = LocalDate.now();
                LocalTime currentTime = LocalTime.now();

                if (!date.equals(currentDate)) {
                    DefaultLogger.logger.info("날짜가 바뀌어 DMALog 불러오기를 중단합니다.");
                    break;
                }

                try {
                    if (currentTime.compareTo(startTime) < 0 || currentTime.compareTo(endTime) > 0) {
                        Thread.sleep(defaultWaitDelay);
                    } else {
                        manager.loadDataAndNotifyForced();
                        Thread.sleep(period);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        RefreshTask(LocalTime startTime, LocalTime endTime, boolean pollingMode, int pollingSec) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.period = defaultWaitDelay;
            this.manager = DMALogDataCenter.Instance;

            if (pollingMode) {
                this.period = pollingSec * 1000;
                this.manager.setPollingMode(true);
            }
        }
    }
}
