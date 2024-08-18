package bulls.feed.dc;

public class ConcreteCodeHashData<T> {
    String code;
    String obName;
    CodeObserver<T> observer;
    T newData;

    public void of(String obName, CodeObserver<T> observer, String code, T newData) {
        this.code = code;
        this.newData = newData;
        this.observer = observer;
        this.obName = obName;
    }
}
