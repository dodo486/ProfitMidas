package bulls.tool.util;

import java.util.concurrent.ConcurrentHashMap;

public class ObjectPool<T> {

    private final ConcurrentHashMap<T, T> objectMap;

    public ObjectPool() {
        objectMap = new ConcurrentHashMap<>();
    }

    public final T getUniqueObject(final T t) {
        if (objectMap.containsKey(t))
            return objectMap.get(t);

        objectMap.put(t, t);
        return t;
    }
}