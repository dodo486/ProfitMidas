package bulls.thread;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class GeneralCoreThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;

    public GeneralCoreThreadFactory() {
        this("gc-pool-" + poolNumber.getAndIncrement() + "-thread", false);
    }

    public GeneralCoreThreadFactory(String namePrefix) {
        this(namePrefix, false);
    }

    public GeneralCoreThreadFactory(String namePrefix, boolean daemon) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @NotNull
    @Override
    public synchronized Thread newThread(@NotNull final Runnable r) {
        String name2 = threadNumber.get() <= 1 ? namePrefix : (namePrefix + '-' + threadNumber.get());
//        DefaultLogger.logger.info("Creating new thread... " + name2 + " cpudId:" + Affinity.getCpu());
        threadNumber.addAndGet(1);
        GeneralCoreThread t = new GeneralCoreThread(r, name2);
        t.setDaemon(daemon);
        return t;
    }
}
