package bulls.designTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map 에 update 가 있을때 do something
 */
public class WorkMap<K, V> implements Map<K, V> {

    private final ConcurrentHashMap<K, V> map;

    private final MapWorker<K, V> onPut;
    private final MapWorker<K, V> onRemove;

    public WorkMap(MapWorker<K, V> onPut, MapWorker<K, V> onRemove) {
        map = new ConcurrentHashMap<>();
        this.onPut = onPut;
        this.onRemove = onRemove;
    }

    public WorkMap(MapWorker<K, V> onPut) {
        map = new ConcurrentHashMap<>();
        this.onPut = onPut;
        this.onRemove = (k, v) -> {
        };
    }

    @Override
    public V put(K key, final V value) {
        V oldValue = map.put(key, value);
        onPut.work(key, value);
//        LazyWorkCenter.instance.executeLazy(onPut);
        return oldValue;
    }

    public V putWithoutWork(K key, final V value) {
        return map.put(key, value);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }


    @Override
    public V remove(Object key) {
        V v = map.remove(key);
        if (v != null) {
            onRemove.work((K) key, v);
//            LazyWorkCenter.instance.executeLazy(onRemove);
        }
        return v;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

}
