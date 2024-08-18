package bulls.tool.util.counter;

public interface SimpleCounter {
    String getKey();

    void addSuccess();

    void addFailed();

    int getSuccessCount();

    int getFailedCount();

    int getTotalCount();

    double getRatio();

    void reset();
}
