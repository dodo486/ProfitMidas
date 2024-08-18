package bulls.feed.dc;

public interface CodeObserver<T> {
    void update(String obName, String codeUpdatedFromMarket, T newData);
}
