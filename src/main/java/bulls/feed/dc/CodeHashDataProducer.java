package bulls.feed.dc;

import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;

public class CodeHashDataProducer<T> {

    private final RingBuffer<ConcreteCodeHashData<T>> ringBuffer;

    public CodeHashDataProducer(RingBuffer<ConcreteCodeHashData<T>> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(String obName, CodeObserver<T> observer, String code, T newData) throws InsufficientCapacityException {
        long sequence = ringBuffer.tryNext();  // Grab the next sequence
        try {
            ConcreteCodeHashData<T> data = ringBuffer.get(sequence); // Get the entry in the Disruptor
            // for the sequence
            data.of(obName, observer, code, newData);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    public void onDataBlocking(String obName, CodeObserver<T> observer, String code, T newData) {
        long sequence = ringBuffer.next();  // Grab the next sequence
        try {
            ConcreteCodeHashData<T> data = ringBuffer.get(sequence); // Get the entry in the Disruptor
            // for the sequence
            data.of(obName, observer, code, newData);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}