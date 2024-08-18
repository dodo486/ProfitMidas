package bulls.designTemplate.observer;

import bulls.datastructure.Pair;
import bulls.log.DefaultLogger;

import java.util.LinkedHashMap;

public class ConcreteObserverStation<T> implements ObserverStation<T> {

    public final String obStationName;

    public ConcreteObserverStation(String obStationName) {
        this.obStationName = obStationName;
    }

    private LinkedHashMap<String, Pair<Observer<T>, Filter<T>>> map = new LinkedHashMap<>();
    public Pair[] obList = new Pair[0];

    @Override
    public void addObserver(String key, Observer<T> o, Filter<T> f) {
        if (o == null) {
            DefaultLogger.logger.error("ConcreteObserverStation can't accept null observer");
            return;
        }

        synchronized (this) {
            map.put(key, new Pair<>(o, f));
            obList = map.values().toArray(Pair[]::new);
        }

        DefaultLogger.logger.info("ObserverStation for {} : Adding new Observer {}  , total registered observer :{}", obStationName, key, map.size());
    }

    public void addObserverOnHead(String key, Observer<T> o, Filter<T> f) {
        if (o == null) {
            DefaultLogger.logger.error("ConcreteObserverStation can't accept null observer");
            return;
        }

        synchronized (this) {
            var oldMap = map;
            map = new LinkedHashMap<>();
            map.put(key, new Pair<>(o, f));
            map.putAll(oldMap);
            obList = map.values().toArray(Pair[]::new);
        }

        DefaultLogger.logger.info("ObserverStation for {} : Adding new Observer {}  , total registered observer :{}", obStationName, key, map.size());
    }

    @Override
    public void deleteObserver(String key) {
        synchronized (this) {
            map.remove(key);
            obList = map.values().toArray(Pair[]::new);
        }

        DefaultLogger.logger.info("ObserverStation for {} : Removing Observer {}  , total registered observer :{}", obStationName, key, map.size());
    }

    @Override
    public Pair<Observer<T>, Filter<T>>[] getObserver() {
        return obList;
    }
}
