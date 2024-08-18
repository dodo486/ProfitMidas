package bulls.thread;

import bulls.staticData.TempConf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


public enum LazyWorkCenter {
    instance;


    LazyWorkCenter() {
        boolean monitorLazyQueue = TempConf.MONITOR_LAZY_QUEUE;
        int workerSize = TempConf.LAZY_WORKER_POOL_SIZE;

        if (monitorLazyQueue)
            lazyWorker = GeneralCoreNamedThreadPoolExecutor.withQSizeMonitor(workerSize, workerSize + 2, 0L, TimeUnit.MILLISECONDS, "LazyWorker");
        else
            lazyWorker = new GeneralCoreNamedThreadPoolExecutor(workerSize, workerSize + 2, 0L, TimeUnit.MILLISECONDS, "LazyWorker");
    }

    private final ExecutorService lazyWorker;


    public void executeLazy(Runnable lazyTask) {
        lazyWorker.execute(lazyTask);
    }


    public boolean waitAndShutdown(int minuteToWait) throws InterruptedException {
        lazyWorker.shutdown();
        final boolean done = lazyWorker.awaitTermination(minuteToWait, TimeUnit.MINUTES);
        return done;
    }

}
