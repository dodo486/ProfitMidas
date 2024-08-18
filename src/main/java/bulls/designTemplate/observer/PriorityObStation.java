package bulls.designTemplate.observer;

import bulls.datastructure.Pair;
import bulls.log.DefaultLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PriorityObStation<T> implements ObserverStation<T> {

    public PriorityObStation(String obStationName) {
        this.obStationName = obStationName;
    }

    public final String obStationName;

    private Pair<Observer<T>, Filter<T>>[] obList = new Pair[0];

    private final ConcurrentHashMap<String, ObserverData<T>> map = new ConcurrentHashMap<>();

    public void addObserverWIthPriority(String key, Observer<T> o, Filter<T> f, int priority) {
        synchronized (this) {
            ObserverData<T> givenData = new ObserverData<>(priority, key, o, f);
            map.put(key, givenData);
            List<ObserverData<T>> sortedDataList = map.values().stream().sorted().collect(Collectors.toList());

            ArrayList<Pair<Observer<T>, Filter<T>>> list = new ArrayList<>();
            sortedDataList.forEach(sortedData -> list.add(new Pair<>(sortedData.ob, sortedData.filter)));

            obList = list.stream().toArray(Pair[]::new);
            DefaultLogger.logger.info("{} : {} Observer Inserted with {} priority , registered observer count :{}", obStationName, key, priority, map.size());
        }
    }

    @Override
    public void addObserver(String key, Observer<T> o, Filter<T> f) {
        if (!Objects.nonNull(o))
            return;

        synchronized (this) {
            int bestKey;
            Optional<Integer> opBestKey = map.values().stream().map(tObserverData -> tObserverData.priority).min(Integer::compare);
            bestKey = opBestKey.orElse(0);

            addObserverWIthPriority(key, o, f, bestKey - 1);
            DefaultLogger.logger.info("{} Observer Inserted , current observer :{}", key, map.size());
        }
    }

    @Override
    public void deleteObserver(String key) {
        synchronized (this) {
            ObserverData<T> previousData = map.remove(key);
            List<ObserverData<T>> sortedDataList = map.values().stream().sorted().collect(Collectors.toList());

            ArrayList<Pair<Observer<T>, Filter<T>>> list = new ArrayList<>();
            sortedDataList.forEach(sortedData -> list.add(new Pair<>(sortedData.ob, sortedData.filter)));
            obList = list.stream().toArray(Pair[]::new);

            DefaultLogger.logger.info("{} Observer deleted, remaining observer: {}", key, map.size());
        }
    }

    @Override
    public Pair<Observer<T>, Filter<T>>[] getObserver() {
        return obList;
    }

    private static class ObserverData<T> implements Comparable<ObserverData<T>> {
        public ObserverData(int priority, String key, Observer<T> ob, Filter<T> filter) {
            this.priority = priority;
            this.key = key;
            this.ob = ob;
            this.filter = filter;
        }

        int priority;
        String key;
        Observer<T> ob;
        Filter<T> filter;

        @Override
        public int compareTo(ObserverData<T> o) {
            return Integer.compare(priority, o.priority);
        }
    }
}
