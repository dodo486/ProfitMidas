package bulls.designTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * map update 될때 기존 value 가 그냥 사라지는 것을 원치 않을때...
 */
public class SkipListMapWithPrevValueHandler<K, V> implements SortedMap<K, V> {

    private final ConcurrentSkipListMap<K, V> map;
    private final MapWorker<K, V> onPrevValueRemoved;
    private MapPutTester<K, V> putTester;

    public SkipListMapWithPrevValueHandler(Comparator<? super K> c, MapWorker<K, V> onPrevValueRemoved) {
        map = new ConcurrentSkipListMap<>(c);
        this.onPrevValueRemoved = onPrevValueRemoved;
    }

    public SkipListMapWithPrevValueHandler(Comparator<? super K> c, MapWorker<K, V> onPrevValueRemoved, MapPutTester<K, V> putTester) {
        map = new ConcurrentSkipListMap<>(c);
        this.onPrevValueRemoved = onPrevValueRemoved;
        this.putTester = putTester;
    }

    @Override
    public Comparator<? super K> comparator() {
        return map.comparator();
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return map.subMap(fromKey, toKey);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return map.headMap(toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return map.tailMap(fromKey);
    }

    @Override
    public K firstKey() {
        if (map.isEmpty())
            return null;
        return map.firstKey();
    }

    @Override
    public K lastKey() {
        if (map.isEmpty())
            return null;
        return map.lastKey();
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
    public V put(K key, V value) {
        boolean isPut = true;
        if (putTester != null)
            isPut = putTester.putTest(key, value);

        if (!isPut)
            return null;

        V previousValue = map.put(key, value);

        if (previousValue != null) {
            onPrevValueRemoved.work(key, previousValue);
        }

        return previousValue;

    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
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
