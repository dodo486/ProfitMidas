package bulls.analysis.contractType;

import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.designTemplate.JsonConvertible;
import bulls.dmaLog.DMALog;
import bulls.dmaLog.enums.DMALogTransactionType;
import bulls.dmaLog.ReportDMALog;
import bulls.dmaLog.TradeDMALog;
import bulls.dmaLog.loader.DMALogDataCenter;
import bulls.dmaLog.transactiontracker.TransactionTracker;
import bulls.order.CodeAndBook;
import bulls.order.enums.LongShort;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ContractTypeChecker implements JsonConvertible {
    private final CodeAndBook codeAndBook;

    private final ConcurrentHashMap<ContractType, Integer> countMap;
    private final ConcurrentHashMap<ContractType, Integer> amountMap;

    public ContractTypeChecker(CodeAndBook codeAndBook) {
        this.codeAndBook = codeAndBook;
        countMap = new ConcurrentHashMap<>();
        amountMap = new ConcurrentHashMap<>();
        for (ContractType type : ContractType.values()) {
            countMap.put(type, 0);
            amountMap.put(type, 0);
        }
    }

    public ContractTypeChecker(String bookCode, String isinCode) {
        this(CodeAndBook.getOrCreate(isinCode, bookCode));
    }

    public void update(TradeDMALog log) {
        int amount = (int) log.getKrxExecutionQuantity();
        if (isBadMakingOrder(log))
            addAmount(ContractType.MAKE_BAD, amount);
        else
            addAmount(isTake(log), amount);
    }

    private void addAmount(ContractType type, int amount) {
        countMap.computeIfPresent(type, (k, v) -> v + 1);
        amountMap.computeIfPresent(type, (k, v) -> v + amount);
    }

    public String getIsinCode() {
        return codeAndBook.getCode();
    }

    public String getBookCode() {
        return codeAndBook.getBookCode();
    }

    @Override
    public String toString() {
        return "ContractTypeChecker{" +
                "codeAndBook=" + codeAndBook +
                ", countMap=" + countMap +
                ", amountMap=" + amountMap +
                '}';
    }

    @Override
    public void fillObjectNode(ObjectNode node) {
        for (var entry : countMap.entrySet()) {
            String typeString = entry.getKey().toString();
            node.put(typeString + "Count", entry.getValue());
        }

        for (var entry : amountMap.entrySet()) {
            String typeString = entry.getKey().toString();
            node.put(typeString + "Amount", entry.getValue());
        }
    }

    public Map<String, Integer> getData() {
        Map<String, Integer> map = new HashMap<>();
        for (var entry : countMap.entrySet()) {
            String typeString = entry.getKey().toString();
            map.put(typeString + "Count", entry.getValue());
        }

        for (var entry : amountMap.entrySet()) {
            String typeString = entry.getKey().toString();
            map.put(typeString + "Amount", entry.getValue());
        }

        return map;
    }

    private static boolean isBadMakingOrder(TradeDMALog log) {
        ConcurrentHashMap<String, ConcurrentLinkedQueue<DMALog>> fullLogMap = DMALogDataCenter.Instance.getOrderIdFullLogMap();
        boolean bad = false;
        // 주문번호 x인 주문이 체결되었을 때, 이에 대한 정정/취소 주문이 존재한다면
        // 주문번호 x', 원주문번호 x 형태일 것이다
        // 따라서 주문번호 x와 연결된 주문을 찾아서 확인해야 한다
        List<DMALog> logList = new ArrayList<>();
        Collection<String> orderIdList = TransactionTracker.Instance.getConnectedOrderIdList(log.getCurrentOrderId());
        if (orderIdList != null) {
            for (String candidateOrderId : orderIdList) {
                logList.addAll(fullLogMap.get(candidateOrderId));
            }
        }

        double price = log.getOrderPrice();

        for (DMALog dmaLog : logList) {
            if (dmaLog instanceof ReportDMALog) {
                ReportDMALog reportDMALog = (ReportDMALog) dmaLog;
                String orderRejectionCode = reportDMALog.getOrderRejectionCode();

                // 체결된 가격과 정정하려는 가격이 같으면
                // 정정이 된 다음 확인이 늦게 도착한 것일 수 있음
                if (price == reportDMALog.getOrderPrice())
                    continue;

                // 불리한 가격으로 정정을 시도하는 경우
                // 정정하기 이전 가격으로 체결되는 것은 take가 아님
                if ((log.getAskBidType() == LongShort.LONG && price < reportDMALog.getOrderPrice()) ||
                        (log.getAskBidType() == LongShort.SHORT && price > reportDMALog.getOrderPrice()))
                    continue;

                if (orderRejectionCode.equals("0804") || orderRejectionCode.equals("0805")) {
                    //reportDMALog.printForHuman();
                    bad = true;
                    break;
                }
            }
        }

        return bad;
    }

    private static ContractType isTake(TradeDMALog log) {
        // 신규인지 정정인지부터 확인
        // 신규이면 이전 주문이 없을 것이므로
        String parentOrderId = TransactionTracker.Instance.getParentOrderId(log.getCurrentOrderId());

        // 신규 주문
        if (parentOrderId == null) {
            var reportLogList = DMALogDataCenter.Instance.getOrderIdReportLogMap().get(log.getCurrentOrderId());
            if (reportLogList == null || reportLogList.isEmpty()) {
                System.out.println(log.getCurrentOrderId() + " 에 대응하는 주문 확인이 없어서 Hit 판별 불가.");
                return ContractType.MAKE_GOOD;
            }

            // 성공한 신규 주문 중 가장 나중 시간을 구한다.
            LocalTime t = LocalTime.MIN;
            ReportDMALog lastReportLog = null;
            for (var reportLog : reportLogList) {
                if (reportLog.getTransactionType() != DMALogTransactionType.CONFIRMED)
                    continue;

                if (reportLog.getTime().compareTo(t) > 0) {
                    t = reportLog.getTime();
                    lastReportLog = reportLog;
                }
            }

            if (lastReportLog == null)
                return ContractType.MAKE_GOOD;

            if (lastReportLog.getOrderPrice() == 0)
                return ContractType.MARKET;

            // 주문 낸 후 1ms 이내 체결되면 Hit
            boolean isTake = ChronoUnit.MICROS.between(t, log.getTime()) <= 1_000;
//            if (isTake)
//                System.out.println("Take!");
//            lastReportLog.printForHuman();
//            System.out.println(log);
//            System.out.println("========================================");

            return isTake ? ContractType.TAKE : ContractType.MAKE_GOOD;
        }

        var reportLogList = DMALogDataCenter.Instance.getOrderIdReportLogMap().get(log.getCurrentOrderId());
        if (reportLogList == null || reportLogList.isEmpty()) {
            System.out.println(log.getCurrentOrderId() + " 에 대응하는 주문 확인이 없어서 Hit 판별 불가.");
            return ContractType.MAKE_GOOD;
        }

        LocalTime t = LocalTime.MIN;
        ReportDMALog lastReportLog = null;
        for (var reportLog : reportLogList) {
            if (reportLog.getTransactionType() != DMALogTransactionType.CONFIRMED)
                continue;

            // 정정 주문 중 현재 체결 주문과 주문번호가 일치하지 않으면 패스
            if (!reportLog.getCurrentOrderId().equals(log.getCurrentOrderId()))
                continue;

            if (reportLog.getTime().compareTo(t) > 0) {
                t = reportLog.getTime();
                lastReportLog = reportLog;
            }
        }

        if (lastReportLog == null)
            return ContractType.MAKE_GOOD;

        if (lastReportLog.getOrderPrice() == 0)
            return ContractType.MARKET;

        boolean isTake = ChronoUnit.MICROS.between(t, log.getTime()) <= 1_000;

//        if (isHit)
//            System.out.println("Hit!");
//        else
//            System.out.println("Make!");
//        lastReportLog.printForHuman();
//        System.out.println(log);
//        System.out.println("========================================");

        // 주문 낸 후 1ms 이내 체결되면 Hit
        return isTake ? ContractType.TAKE : ContractType.MAKE_GOOD;
    }

    public static ContractType getType(TradeDMALog log) {
        if (isBadMakingOrder(log))
            return ContractType.MAKE_BAD;

        return isTake(log);
    }
}
