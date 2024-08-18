package bulls.tool.util;

public class PeriodicRunnable implements Comparable<PeriodicRunnable> {
    final String key;
    Runnable work;
    int periodInSec;
    double executionPriority;

    public PeriodicRunnable(String key, Runnable r, int periodInSec, double executionPriority) {
        this.key = key;
        this.work = r;
        this.periodInSec = periodInSec;
        this.executionPriority = executionPriority;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeriodicRunnable that = (PeriodicRunnable) o;
        return key.equals(that.key);
    }

    @Override
    public int compareTo(PeriodicRunnable o) {
        int c = Double.compare(executionPriority, o.executionPriority);
        if (c == 0)
            return this.key.compareTo(o.key);
        return c;
    }

    @Override
    public String toString() {
        String sb = key +
                " per sec :" +
                periodInSec +
                " priority: " +
                executionPriority;
        return sb;
    }
}
