package bulls.designTemplate;

import java.util.HashMap;
import java.util.Map;

public class HeterogeneousContainer {
    private final Map<Class<?>, Object> map = new HashMap<>();


    public <T> void put(Class<T> t, T o) {
        map.putIfAbsent(t, o);
    }

    public <T> T get(Class<T> t) {
        return t.cast(map.get(t));
    }
}
