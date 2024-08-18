package bulls.designTemplate.COR;

import java.util.LinkedList;

public class ChainOfResponsibility<T extends Responsibility<V>, V> {

    private final LinkedList<T> chain;

    private volatile boolean isFinalized = false;

    public ChainOfResponsibility(T initHandler) {
        chain = new LinkedList<>();
        chain.add(initHandler);
    }

    public ChainOfResponsibility() {
        chain = new LinkedList<>();
    }

    public void finalizeChain(T finalHandler) {
        append(finalHandler);
        finalizeChain();
    }

    public void finalizeChain() {
        isFinalized = true;
    }

    public synchronized void append(T next) {
        if (isFinalized)
            throw new IllegalStateException("This chain is already finalized ");

        chain.getLast().next = next;
        chain.add(next);
    }

    public void start(V v) {
        if (chain.size() == 0)
            throw new IllegalStateException("Chain Responsibility need at least one responsibility");

        chain.getFirst().doWork(v);
    }

}
