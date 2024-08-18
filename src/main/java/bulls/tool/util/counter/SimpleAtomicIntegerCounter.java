package bulls.tool.util.counter;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleAtomicIntegerCounter implements SimpleCounter {
    private final String key;
    private final AtomicInteger successCount;
    private final AtomicInteger failedCount;

    public SimpleAtomicIntegerCounter(String key) {
        this.key = key;
        successCount = new AtomicInteger(0);
        failedCount = new AtomicInteger(0);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void addSuccess() {
        successCount.incrementAndGet();
    }

    @Override
    public void addFailed() {
        failedCount.incrementAndGet();
    }

    @Override
    public int getSuccessCount() {
        return successCount.get();
    }

    @Override
    public int getFailedCount() {
        return failedCount.get();
    }

    @Override
    public int getTotalCount() {
        return successCount.get() + failedCount.get();
    }

    @Override
    public double getRatio() {
        int successCount = this.successCount.get();
        int failedCount = this.failedCount.get();
        if (successCount + failedCount == 0)
            return 0;
        return (double) successCount / (successCount + failedCount);
    }

    @Override
    public void reset() {
        successCount.set(0);
        failedCount.set(0);
    }
}
