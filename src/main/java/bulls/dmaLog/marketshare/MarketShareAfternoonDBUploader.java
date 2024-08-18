package bulls.dmaLog.marketshare;

import bulls.dmaLog.loader.DMALogDataCenter;
import bulls.thread.GeneralCoreThread;

import java.time.LocalDate;
import java.time.LocalTime;

public enum MarketShareAfternoonDBUploader {

    Instance;

    public void threadRun() {
        LocalTime defaultRefreshTime = LocalTime.of(15, 45, 0);
        threadRun(defaultRefreshTime);
    }

    public void threadRun(LocalTime refreshTime) {
        new GeneralCoreThread(new RefreshTask(refreshTime)).start();
    }

    public class RefreshTask implements Runnable {
        private LocalDate date;
        private final LocalTime refreshTime;
        private final DMALogDataCenter manager;
        private boolean refresh = false;

        private final int defaultWaitDelay = 10 * 60 * 1000; // 10 min

        @Override
        public void run() {
            date = LocalDate.now();
            while (true) {
                LocalDate currentDate = LocalDate.now();
                LocalTime currentTime = LocalTime.now();

                if (!date.equals(currentDate)) {
                    refresh = false;
                }

                try {
                    if (refreshTime.compareTo(currentTime) < 0 && !refresh) {
                        manager.loadDataAndNotifyForced();
                        MarketVolumeShareUpdater.Instance.loadDataAndSendToDB();
                        MarketTradingValueShareUpdater.Instance.setKeyName("account").loadDataAndSendToDB();
                        MarketTradingValueShareUpdater.Instance.setKeyName("bookCode").loadDataAndSendToDB();
                        refresh = true;
                    } else
                        Thread.sleep(defaultWaitDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        RefreshTask(LocalTime refreshTime) {
            this.refreshTime = refreshTime;
            this.manager = DMALogDataCenter.Instance;
        }
    }
}