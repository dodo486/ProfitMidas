package bulls.tool.util.counter;

public class SimpleIntegerCounter implements SimpleCounter {
    private final String key;
    private int successCount, failedCount;

    public SimpleIntegerCounter(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void addSuccess() {
        successCount++;
    }

    @Override
    public void addFailed() {
        failedCount++;
    }

    @Override
    public int getSuccessCount() {
        return successCount;
    }

    @Override
    public int getFailedCount() {
        return failedCount;
    }

    @Override
    public int getTotalCount() {
        return successCount + failedCount;
    }

    @Override
    public double getRatio() {
        if (successCount + failedCount == 0)
            return 0;

        return (double) successCount / (successCount + failedCount);
    }

    @Override
    public void reset() {
        successCount = failedCount = 0;
    }
}
