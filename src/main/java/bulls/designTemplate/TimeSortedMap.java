package bulls.designTemplate;

import java.time.LocalTime;
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListMap;

public class TimeSortedMap<T extends HasTime> {
    private final ConcurrentSkipListMap<LocalTime, T> map;

    public TimeSortedMap() {
        map = new ConcurrentSkipListMap<>();
    }

    public void put(T data) {
        LocalTime currentTime = data.getTime();
        if (map.containsKey(currentTime)) {
            LocalTime newTime = map.lowerKey(currentTime.plusNanos(1000)).plusNanos(1);
            data.setTime(newTime);
            currentTime = newTime;
        }

        map.put(currentTime, data);
    }

    public void putAll(Collection<? extends T> dataCollection) {
        for (T data : dataCollection)
            put(data);
    }

    public T get(LocalTime t) {
        return map.get(t);
    }

    public Collection<T> values() {
        return map.values();
    }

    public LocalTime lastKey() {
        return map.lastKey();
    }

    // 주어진 t 이전값
    public LocalTime lowerKey(LocalTime t) {
        return map.lowerKey(t);
    }

    // 주어진 t 이하값
    public LocalTime floorKey(LocalTime t) {
        return map.floorKey(t);
    }

    // 주어진 t 이상값
    public LocalTime ceilingKey(LocalTime t) {
        return map.ceilingKey(t);
    }

    // 주어진 t 다음값
    public LocalTime higherKey(LocalTime t) {
        return map.higherKey(t);
    }

    public void clear() {
        map.clear();
    }
}