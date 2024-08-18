package bulls.websocket.asyncHandling;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class ClassSingletonMap<T> {
    private final ConcurrentHashMap<Class<? extends T>, T> functionMap = new ConcurrentHashMap<>();
    private final T defaultFunction;

    public ClassSingletonMap(T defaultFunction) {
        this.defaultFunction = defaultFunction;
    }

    public T getFunc(Class<? extends T> clazz) {
        return functionMap.computeIfAbsent(clazz, c -> {
            try {
                var constructor = clazz.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return defaultFunction;
        });
    }

    public boolean isDefaultFunction(T func) {
        return defaultFunction == func;
    }
}
