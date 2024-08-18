package bulls.feed.dc;

import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CodeEventHashMap<V> implements Map<String, V> {

    protected ConcurrentHashMap<String, V> map;
    protected CodeHashObStation<V> obStation;


    public CodeEventHashMap(String purpose, int workerSize, ProducerType producerType, WaitStrategy waitStrategy, Collection<EmergencyHandler> emergencyHandlerCollection) {
        map = new ConcurrentHashMap<>();
        obStation = new CodeHashObStation<>(purpose, workerSize, producerType, waitStrategy, emergencyHandlerCollection);
    }

    public CodeEventHashMap() {
        map = new ConcurrentHashMap<>();
        obStation = new CodeHashObStation<>();
    }

    // 한 observer가 여러 code 의 업데이트를 동시에 다 받아야 할때
    public void addMultiCodeObserver(Set<String> codeSet, CodeObserver<V> observer, String obName) {
        obStation.addMultiCodeObserver(codeSet, observer, obName);
    }

    // 기존에 다른 code가 같은 옵저버 이름으로 등록되어 있다면 해당 옵저버를 지운다.
    public void addObserver(String code, CodeObserver<V> observer, String obName) {
        obStation.addObserver(code, observer, obName);
    }

    public void deleteObserver(String code, String obName) {
        obStation.removeObserver(code, obName);
    }

    @Override
    public V put(String code, final V value) {
        V oldValue = map.put(code, value);
        obStation.notify(code, value);


        return oldValue;
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
    public void putAll(Map<? extends String, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return map.entrySet();
    }

    public String getObStationString() {
        return obStation.toString();
    }
}
