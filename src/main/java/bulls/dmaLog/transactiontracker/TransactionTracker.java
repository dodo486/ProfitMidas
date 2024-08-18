package bulls.dmaLog.transactiontracker;

import bulls.designTemplate.observer.Observer;
import bulls.dmaLog.DMALog;
import bulls.dmaLog.DMALogList;
import bulls.dmaLog.FEPConstantValue;
import bulls.dmaLog.loader.DMALogDataCenter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum TransactionTracker implements Observer<DMALogList> {
    Instance;

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> connectionGraph;
    private ConcurrentHashMap<String, String> reverseConnectionGraph;

    private boolean isActive = false;

    public void init() {
        isActive = true;
        connectionGraph = new ConcurrentHashMap<>();
        reverseConnectionGraph = new ConcurrentHashMap<>();
    }

    public void set(DMALogList logListObj) {
        init();
        update(logListObj);
    }

    public void update(DMALogList logListObj) {
        for (DMALog log : logListObj.getLogList()) {
            String orderId = log.getCurrentOrderId();
            String originalOrderId = log.getOriginalOrderId();
            if (!originalOrderId.equals(FEPConstantValue.ORDER_ID_ZERO) && !originalOrderId.equals(FEPConstantValue.ORDER_ID_NEW)) {
                if (!reverseConnectionGraph.containsKey(orderId)) {
                    connectionGraph.computeIfAbsent(originalOrderId, id -> new ConcurrentLinkedQueue<>()).add(orderId);
                    reverseConnectionGraph.put(orderId, originalOrderId);
                }
            }
        }
    }

    public String getParentOrderId(String childOrderId) {
        return reverseConnectionGraph.get(childOrderId);
    }

    public String getFirstOrderId(String orderId) {
        String head = orderId;
        while (reverseConnectionGraph.containsKey(head)) {
            head = reverseConnectionGraph.get(head);
        }

        return head;
    }

    public List<DMALog> track(String orderId) {
        //System.out.println("Track " + orderId);
        LinkedList<DMALog> orderChain;
        String trackId, head;

        head = getFirstOrderId(orderId);

        LinkedList<String> queue = new LinkedList<>();
        orderChain = new LinkedList<>();

        queue.add(head);

        var orderIdFullLogMap = DMALogDataCenter.Instance.getOrderIdFullLogMap();

        while (queue.size() >= 1) {
            trackId = queue.poll();

            if (orderIdFullLogMap.containsKey(trackId)) {
                orderChain.addAll(orderIdFullLogMap.get(trackId));
            }

            if (connectionGraph.containsKey(trackId)) {
                queue.addAll(connectionGraph.get(trackId));
            }
        }

        Collections.sort(orderChain);
        //System.out.println("Result : " + orderChain.size());
        return orderChain;
    }

    public Collection<String> getConnectedOrderIdList(String orderId) {
        return connectionGraph.get(orderId);
    }

    public void validTrackTest() {
        Set<String> checkSet = new HashSet<>();

        int logCounterChain, logCounter;

        logCounterChain = logCounter = 0;

        System.out.println("validTrackTest Start");

        for (Map.Entry<String, ConcurrentLinkedQueue<DMALog>> entry : DMALogDataCenter.Instance.getOrderIdFullLogMap().entrySet()) {
            String orderId = entry.getKey();
            ConcurrentLinkedQueue<DMALog> logList = entry.getValue();
            if (!checkSet.contains(orderId)) {
                List<DMALog> chain = track(orderId);
                for (DMALog log : chain) {
                    checkSet.add(log.getCurrentOrderId());
                }
                logCounterChain += chain.size();
            }

            logCounter += logList.size();
            System.out.println(logCounter + ", " + logCounterChain);
        }

        System.out.println("Original : " + logCounter);
        System.out.println("Chain : " + logCounterChain);
    }

    public boolean isActive() {
        return isActive;
    }
}

