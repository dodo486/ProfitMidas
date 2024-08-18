package bulls.designTemplate;

public interface ObjectGenerator<T> {
    T next();

    boolean init();

    boolean close();
}
