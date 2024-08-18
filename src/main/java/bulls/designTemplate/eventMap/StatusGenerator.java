package bulls.designTemplate.eventMap;

/**
 * Strategy Pattern
 */
@FunctionalInterface
public interface StatusGenerator<V, E> {
    /**
     * @param o1 old value/status/object
     * @param o2 new value/status/object
     * @return returns status which is describing notifyClient from o1 to o2
     */
    E createStatus(V o1, V o2);
}
