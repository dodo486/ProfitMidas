package bulls.thread;

import bulls.log.DefaultLogger;
import net.openhft.affinity.Affinity;
import net.openhft.affinity.AffinityLock;

/**
 * GeneralCore에서 실행되는 쓰레드를 생성함.
 */
public class GeneralCoreThread extends Thread {
    Thread innerThread;

    public GeneralCoreThread(String name, Runnable target) {
        super(target, name);
        DefaultLogger.logger.info("GeneralCoreThread [{}] is created in CPU {} ! current stack:\n{}", name, Affinity.getCpu(), ThreadUtil.getStackTraceString());
    }

    public GeneralCoreThread(Runnable target, String name) {
        super(target, name);
        DefaultLogger.logger.info("GeneralCoreThread [{}] is created in CPU {} ! current stack:\n{}", name, Affinity.getCpu(), ThreadUtil.getStackTraceString());
    }

    public GeneralCoreThread(Runnable target) {
        super(target);
        DefaultLogger.logger.info("GeneralCoreThread [{}] is created in CPU {} ! current stack:\n{}", this.getName(), Affinity.getCpu(), ThreadUtil.getStackTraceString());
    }

    @Override
    public void run() {
//        DefaultLogger.logger.info("GeneralCoreThread.run() requested : {} cpuId : {}", this.getName(), Affinity.getCpu());
        if (Affinity.getCpu() >= 0) {
            assert (Affinity.getAffinity().get(Affinity.getCpu()));
            if (!Affinity.getAffinity().get(Affinity.getCpu()))
                DefaultLogger.logger.error("GeneralCoreThread is running on isolated core. thread : {} affinity : {}  cpuId : {}", this.getName(), Affinity.getAffinity(), Affinity.getCpu());
        }
        super.run();
    }

    @Override
    public synchronized void start() {
//        DefaultLogger.logger.info("GeneralCoreThread.start() requested : {} {} {}", this.getName(), Affinity.getCpu(), Affinity.getAffinity().toString());
        innerThread = new Thread(() -> {
            if (Affinity.getCpu() >= 0 && !AffinityLock.BASE_AFFINITY.get(Affinity.getCpu())) {
                //not running on general core
                DefaultLogger.logger.info("GeneralCoreThread.start() change affinity : {} {} {}", this.getName(), Affinity.getCpu(), Affinity.getAffinity().toString());
                Affinity.resetToBaseAffinity();
            } else {
                DefaultLogger.logger.info("GeneralCoreThread.start() already running on general core : {} {} {}", this.getName(), Affinity.getCpu(), Affinity.getAffinity().toString());
            }
            super.start();
        });
        innerThread.start();
    }

    public void joinInnerThread() throws InterruptedException {
        if (innerThread != null)
            innerThread.join();
        this.join();
    }
}
