package bulls.thread;

import java.util.List;
import java.util.concurrent.*;

public class GeneralCoreTimeoutThreadPoolExecutor extends ThreadPoolExecutor {
    private final long timeout;
    private final TimeUnit timeoutUnit;

    private final ScheduledExecutorService timeoutExecutor = GeneralCoreExecutors.newSingleThreadScheduledExecutor();
    private final ConcurrentMap<Runnable, ScheduledFuture> runningTasks = new ConcurrentHashMap<>();


    private static final String THREAD_NAME_PATTERN = "%s-%d";

    public GeneralCoreTimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, long timeout, TimeUnit timeoutUnit, String namePrefix) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<>(), new GeneralCoreThreadFactory(namePrefix));
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public GeneralCoreTimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, RejectedExecutionHandler handler, long timeout, TimeUnit timeoutUnit, String namePrefix) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<>(), new GeneralCoreThreadFactory(namePrefix), handler);
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    @Override
    public void shutdown() {
        timeoutExecutor.shutdown();
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        timeoutExecutor.shutdownNow();
        return super.shutdownNow();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (timeout > 0) {
            final ScheduledFuture<?> scheduled = timeoutExecutor.schedule(new TimeoutTask(t), timeout, timeoutUnit);
            runningTasks.put(r, scheduled);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        ScheduledFuture timeoutTask = runningTasks.remove(r);
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
    }

    class TimeoutTask implements Runnable {
        private final Thread thread;

        public TimeoutTask(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            thread.interrupt();
        }
    }
}