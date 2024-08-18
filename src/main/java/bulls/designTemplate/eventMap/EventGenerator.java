package bulls.designTemplate.eventMap;

public interface EventGenerator<K, V, S, E> {
    E generateEvent(K k, V vOld, V vNew, S s);
}
