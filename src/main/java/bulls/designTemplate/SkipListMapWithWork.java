package bulls.designTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class SkipListMapWithWork<K, V> implements NavigableMap<K, V> {

    private final ConcurrentSkipListMap<K, V> map;

    private final MapWorker<K, V> onPut;
    private final MapWorker<K, V> onRemove;

    public SkipListMapWithWork(Comparator<? super K> comparator, MapWorker<K, V> onPut, MapWorker<K, V> onRemove) {
        map = new ConcurrentSkipListMap<>(comparator);
        this.onPut = onPut;
        this.onRemove = onRemove;
    }

    public SkipListMapWithWork(Comparator<? super K> comparator, MapWorker<K, V> onPut) {
        map = new ConcurrentSkipListMap<>(comparator);
        this.onPut = onPut;
        this.onRemove = (k, v) -> {
        };
    }

    @Override
    public K firstKey(){
        return map.firstKey();
    }

    @Override
    public Entry<K, V> lowerEntry(K key) {
        return map.lowerEntry(key);
    }

    @Override
    public K lowerKey(K key) {
        return map.lowerKey(key);
    }

    @Override
    public Entry<K, V> floorEntry(K key) {
        return map.floorEntry(key);
    }

    @Override
    public K floorKey(K key) {
        return map.floorKey(key);
    }

    @Override
    public Entry<K, V> ceilingEntry(K key) {
        return map.ceilingEntry(key);
    }

    @Override
    public K ceilingKey(K key) {
        return map.ceilingKey(key);
    }

    @Override
    public Entry<K, V> higherEntry(K key) {
        return map.higherEntry(key);
    }

    @Override
    public K higherKey(K key) {
        return map.higherKey(key);
    }

    @Override
    public Entry<K,V> firstEntry(){
        return map.firstEntry();
    }

    @Override
    public Entry<K,V> pollFirstEntry(){
        return map.pollFirstEntry();
    }


    @Override
    public K lastKey(){
        return map.lastKey();
    }

    @Override
    public Entry<K,V> lastEntry(){
        return map.lastEntry();
    }

    @Override
    public Entry<K,V> pollLastEntry(){
        return map.pollLastEntry();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return map.descendingMap();
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return map.navigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return map.descendingKeySet();
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return map.subMap(fromKey, fromInclusive, toKey, toInclusive);
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return map.headMap(toKey, inclusive);
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return map.tailMap(fromKey, inclusive);
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
    public NavigableSet<K> keySet() {
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
