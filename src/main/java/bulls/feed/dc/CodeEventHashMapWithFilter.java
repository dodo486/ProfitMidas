package bulls.feed.dc;

import bulls.designTemplate.eventMap.StatusFilter;
import bulls.designTemplate.eventMap.StatusGenerator;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.Collection;

public class CodeEventHashMapWithFilter<V, S> extends CodeEventHashMap<V> {

    protected StatusGenerator<V, S> statusGenerator;
    protected StatusFilter<S> statusFilter;

    public CodeEventHashMapWithFilter(StatusGenerator<V, S> sg, StatusFilter<S> sf, String purpose, int workerSize, ProducerType producerType, WaitStrategy waitStrategy, Collection<EmergencyHandler> emergencyHandlerCollection) {
        super(purpose, workerSize, producerType, waitStrategy, emergencyHandlerCollection);
        updateGeneratorAndFilter(sg, sf);
    }

    public CodeEventHashMapWithFilter(StatusGenerator<V, S> sg, StatusFilter<S> sf) {
        updateGeneratorAndFilter(sg, sf);
    }

    public void updateGeneratorAndFilter(StatusGenerator<V, S> sg, StatusFilter<S> sf) {
        this.statusFilter = sf;
        this.statusGenerator = sg;
    }

    @Override
    public V put(String code, final V value) {
//        V oldValue = map.get(code);
//        map.put(code, value);
        V oldValue = map.put(code, value);

        if (oldValue == null) {
            oldValue = value;
        }

        if (statusGenerator != null) {
            S status = statusGenerator.createStatus(oldValue, value);
            if (statusFilter.filterStatus(status))
                obStation.notify(code, value);
        } else {
            obStation.notify(code, value);
        }

        return oldValue;
    }

    public void forceNotify(String code, V newValue) {
        obStation.notify(code, newValue);
    }

    @Override
    public String toString() {
        return obStation.toString();
    }

}
