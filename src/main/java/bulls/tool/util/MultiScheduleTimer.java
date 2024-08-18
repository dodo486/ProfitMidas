package bulls.tool.util;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import bulls.hephaestus.HephaLogType;
import bulls.hephaestus.document.ServerMsgDoc;
import bulls.log.DefaultLogger;
import bulls.thread.LazyWorkCenter;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

public enum MultiScheduleTimer {
    Instance;

    final GeneralCoreTimer t;
    int secCounter = 0;

    //    SetMultimap<Integer, PeriodicRunnable> mapByPeriod = HashMultimap.create();
    final SortedSetMultimap<Integer, PeriodicRunnable> mapByPeriod = TreeMultimap.create(Integer::compareTo, PeriodicRunnable::compareTo);

    MultiScheduleTimer() {
        t = new GeneralCoreTimer(this.getClass().getSimpleName());
        t.scheduleAtFixedRate(
                () -> {
                    secCounter++;
                    synchronized (mapByPeriod) {
                        for (int period : mapByPeriod.keySet()) {
                            if (secCounter % period == 0) {
//                                DefaultLogger.logger.debug("========= {} 초 경과, {} 초 주기 등록된 멀티맵 총 갯수 :{}", secCounter, period, mapByPeriod.size());
                                mapByPeriod.get(period).stream().forEach(pr -> {
//                                    DefaultLogger.logger.info(" {} 를 키로 하는 periodicConsumer 처리", pr.key);
                                    LazyWorkCenter.instance.executeLazy(pr.work);
                                });
                            }
                        }
                    }
                }, 0, 1000);
    }

    public void registerPeriodic(PeriodicRunnable pr) {
        synchronized (mapByPeriod) {
            if (pr.periodInSec == 0) {
                ServerMsgDoc.now(HephaLogType.주의, "periodicScheduler", "Period 는 0 이 될 수 없습니다.");
                return;
            }
            if (mapByPeriod.get(pr.periodInSec).contains(pr)) {
                SortedSet<PeriodicRunnable> ss = mapByPeriod.get(pr.periodInSec);
                ss.forEach(pre -> DefaultLogger.logger.info("기존 pr : {}", pre.toString()));
            }
            mapByPeriod.put(pr.periodInSec, pr);
            DefaultLogger.logger.info("{} 등록 완료 size:{}", pr.key, mapByPeriod.size());
            for (Map.Entry<Integer, Collection<PeriodicRunnable>> en : mapByPeriod.asMap().entrySet()) {
                DefaultLogger.logger.info("{} Periodic runs per every {} seconds", en.getValue().size(), en.getKey());
            }
        }
    }

    public void unRegisterPeriodic(PeriodicRunnable pr) {
        synchronized (mapByPeriod) {
            mapByPeriod.remove(pr.periodInSec, pr);
        }
    }

    public static void main(String[] args) {
        PeriodicRunnable pr1 = new PeriodicRunnable("5Sec", () -> DefaultLogger.logger.debug("every 5 sec with priority 1"), 5, 1);
        PeriodicRunnable pr2 = new PeriodicRunnable("10Sec", () -> DefaultLogger.logger.debug("every 10 sec with priority 2"), 10, 2);
        PeriodicRunnable pr3 = new PeriodicRunnable("5Sec2", () -> DefaultLogger.logger.debug("every 5 sec No.2 with priority 3"), 5, 3);
        PeriodicRunnable pr4 = new PeriodicRunnable("20Sec", () -> DefaultLogger.logger.debug("every 20 sec with priority 4"), 20, 4);


        MultiScheduleTimer.Instance.registerPeriodic(pr2);
        MultiScheduleTimer.Instance.registerPeriodic(pr1);
        MultiScheduleTimer.Instance.registerPeriodic(pr4);
        MultiScheduleTimer.Instance.registerPeriodic(pr3);
    }
}
