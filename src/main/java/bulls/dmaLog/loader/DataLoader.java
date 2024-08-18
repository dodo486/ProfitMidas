package bulls.dmaLog.loader;

public interface DataLoader<T> {
    boolean init();
    T load();
}
