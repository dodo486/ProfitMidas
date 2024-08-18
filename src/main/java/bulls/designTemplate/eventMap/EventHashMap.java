package bulls.designTemplate.eventMap;

import bulls.designTemplate.observer.Filter;
import bulls.designTemplate.observer.Observer;
import bulls.designTemplate.observer.ObserverStation;
import bulls.designTemplate.observer.PriorityObStation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Key,Value,Status
 * data storing 과 동시에 해당 데이터로부터의 status 생성이 목적
 * StatusGenerator : Strategy Pattern , status 생성 로직 담당
 * Map 자체가 Observer pattern 의 observable object 에 해당
 * 임의의 Key 가 업데이트 될 때 EventGen 에 의해 status 가 생성 되어 obStation 에 등록된 observer 로 전달
 * ExecutorService 를 지정하면 발생한 이벤트에 대한 캐치 work 는 해당 스레드 풀에서 처리하고 만약 지정하지 않는다면 EventHashMap 에 put 한 스레드에서 이벤트 처리까지 담당한다.
 */
public class EventHashMap<K, V, S> implements Map<K, V> {

    private final ConcurrentHashMap<K, V> map;
    private final StatusGenerator<V, S> eventGenerator;
    private final ObserverStation<MapUpdateEvent<K, V, S>> obStation;
    private final ExecutorService eventWorkerPool;
    private final StatusFilter<S> filter;


    public EventHashMap(StatusGenerator<V, S> eventGenerator, ExecutorService eventWorkerPool, String obStationName, StatusFilter<S> filter) {
        map = new ConcurrentHashMap<>();
        obStation = new PriorityObStation<>(obStationName);
        this.eventGenerator = eventGenerator;
        this.eventWorkerPool = eventWorkerPool;
        this.filter = filter;
    }

    public EventHashMap(StatusGenerator<V, S> eventGenerator, String obStationName) {
        map = new ConcurrentHashMap<>();
        obStation = new PriorityObStation<>(obStationName);
        this.eventGenerator = eventGenerator;
        this.eventWorkerPool = null;
        this.filter = (e) -> true;
    }

    public EventHashMap(StatusGenerator<V, S> eventGenerator, String obStationName, StatusFilter<S> filter) {
        map = new ConcurrentHashMap<>();
        obStation = new PriorityObStation<>(obStationName);
        this.eventGenerator = eventGenerator;
        this.eventWorkerPool = null;
        this.filter = filter;
    }

    public int getObserverCount() {
        return obStation.getObserver().length;
    }

    public void addObserver(String key, Observer<MapUpdateEvent<K, V, S>> observer, Filter<MapUpdateEvent<K, V, S>> filter) {
        obStation.addObserver(key, observer, filter);
    }

    public void deleteObserver(String key) {
        obStation.deleteObserver(key);
    }

    @Override
    public V put(K key, final V value) {
        V oldValue = map.get(key);
        map.put(key, value);

        if (oldValue == null)
            return null;

        MapUpdateEvent<K, V, S> event;
        S status = eventGenerator.createStatus(oldValue, value);

        if (!filter.filterStatus(status))
            return oldValue;

        event = new MapUpdateEvent<>(key, oldValue, value, status);

        if (eventWorkerPool == null)
            obStation.filterAndNotifyAll(event);
        else
            obStation.filterAndNotifyAll(event, eventWorkerPool);

        return oldValue;
    }

    public V putWithoutEvent(K key, final V value) {
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
        return map.remove(key);
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
