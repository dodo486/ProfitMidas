package bulls.designTemplate.observer;

import bulls.datastructure.Pair;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public interface ObserverStation<T> {
    void addObserver(String key, Observer<T> o, Filter<T> f);

    void deleteObserver(String key);

    Pair<Observer<T>, Filter<T>>[] getObserver();


    default void filterAndNotifyExclusive(T data) {
        if (data == null)
            return;

        for (Pair<Observer<T>, Filter<T>> p : this.getObserver()) {
            Filter<T> f = p.secondElem;
            if (!f.filter(data))
                continue;
            p.firstElem.update(data);
            break; // notify exclusive... immediately break from loop
        }
    }

    default void filterAndNotifyAll(T data) {
        if (data == null)
            return;

        for (Pair<Observer<T>, Filter<T>> p : this.getObserver()) {
            Filter<T> f = p.secondElem;
            if (!f.filter(data))
                continue;
            p.firstElem.update(data);
        }
    }

    default void filterAndNotifyAllWithExecutor(T data, Executor executor) {
        if (data == null)
            return;

        for (Pair<Observer<T>, Filter<T>> p : this.getObserver()) {
            Runnable feedWork = () -> {
                Filter<T> f = p.secondElem;
                if (!f.filter(data))
                    return;
                p.firstElem.update(data);
            };
            executor.execute(feedWork);
        }
    }

    default void notifyAll(T data) {
        if (data == null)
            return;

        for (Pair<Observer<T>, Filter<T>> p : this.getObserver()) {
            p.firstElem.update(data);
        }
    }

    default void filterAndNotifyExclusive(T data, ExecutorService worker) {
        if (data == null)
            return;

        // use findFirst for performance cuz lambda is lazy.
        for (Pair<Observer<T>, Filter<T>> p : this.getObserver()) {
            Filter<T> f = p.secondElem;
            if (!f.filter(data))
                continue;

            Runnable work = () -> p.firstElem.update(data);
            worker.submit(work);
            break; // notify exclusive... immediately break from loop
        }
    }

    default void filterAndNotifyAll(T data, ExecutorService worker) {
        if (data == null)
            return;

        for (Pair<Observer<T>, Filter<T>> p : this.getObserver()) {
            Filter<T> f = p.secondElem;
            if (!f.filter(data))
                continue;

            Runnable work = () -> p.firstElem.update(data);
            worker.submit(work);
        }
    }

    default void notifyAll(T data, ExecutorService worker) {
        if (data == null)
            return;

        for (Pair<Observer<T>, Filter<T>> p : this.getObserver()) {
            Runnable work = () -> p.firstElem.update(data);
            worker.submit(work);
        }
    }
}
