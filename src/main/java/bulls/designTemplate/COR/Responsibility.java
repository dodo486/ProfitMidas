package bulls.designTemplate.COR;

public abstract class Responsibility<V> {
    public Responsibility<V> next;

    public final void doWork(V info) {
        if (tryToHandle(info))
            return;

        next.doWork(info);
    }

    public abstract boolean tryToHandle(V work);
}
