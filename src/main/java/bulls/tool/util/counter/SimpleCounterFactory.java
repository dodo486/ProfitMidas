package bulls.tool.util.counter;

import java.util.concurrent.ConcurrentHashMap;

public abstract class SimpleCounterFactory {
    private static final ConcurrentHashMap<String, SimpleIntegerCounter> counterMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, SimpleAtomicIntegerCounter> atomicCounterMap = new ConcurrentHashMap<>();

    public static SimpleCounter get(String key) {
        return counterMap.computeIfAbsent(key, SimpleIntegerCounter::new);
    }

    public static SimpleCounter getAtomic(String key) {
        return atomicCounterMap.computeIfAbsent(key, SimpleAtomicIntegerCounter::new);
    }
}
