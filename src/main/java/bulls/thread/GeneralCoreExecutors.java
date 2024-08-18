package bulls.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class GeneralCoreExecutors {
    public static ExecutorService newSingleThreadExecutor() {
        ExecutorService ret = Executors.newSingleThreadExecutor(new GeneralCoreThreadFactory());
        return ret;
    }
    public static ExecutorService newFixedThreadPool(int nThreads) {
        ExecutorService ret = Executors.newFixedThreadPool(nThreads, new GeneralCoreThreadFactory());
        return ret;
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        ScheduledExecutorService ret = Executors.newSingleThreadScheduledExecutor(new GeneralCoreThreadFactory());
        return ret;
    }
}
