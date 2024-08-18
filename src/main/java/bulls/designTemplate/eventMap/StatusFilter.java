package bulls.designTemplate.eventMap;

@FunctionalInterface
public interface StatusFilter<S> {
    boolean filterStatus(S s);
}
