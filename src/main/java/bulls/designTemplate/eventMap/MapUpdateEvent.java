package bulls.designTemplate.eventMap;

public class MapUpdateEvent<K, V, S> {

    public final K key;
    public final V oldValue;
    public final V newValue;
    public final S status;

    public MapUpdateEvent(K key, V oldValue, V newValue, S status) {
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.status = status;
    }

}
