package bulls.dmaLog.shortSell;

import bulls.dmaLog.*;
import bulls.dmaLog.enums.DMALogTransactionType;
import bulls.order.enums.LongShort;
import bulls.order.enums.ShortSellCode;
import bulls.order.enums.정정취소구분;
import bulls.staticData.AliasManager;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import org.bson.Document;

public class DMALogInterpreter {
    private BalanceMaster balanceMaster;
    private DocumentMaster documentMaster;

    public DMALogInterpreter() {
    }

    public void setBalanceMaster(BalanceMaster balanceMaster) {
        this.balanceMaster = balanceMaster;
    }

    public void setDocumentMaster(DocumentMaster documentMaster) {
        this.documentMaster = documentMaster;
    }

    /**
     * <h2>제출한 주문의 공매도 표시 검증</h2>
     *
     * <p>제출한 주문의 공매도 표시가 정확한지 확인한다.</p>
     * <p>공매도 표시는 신규 매도 주문 제출일 경우만 확인한다.</p>
     *
     * @param log DMA Log
     * @return 제출한 주문의 공매도 표시가 정확한지 여부
     */
    public boolean checkShortSellCode(DMALog log) {
        // 현물 주문만 처리
        String isinCode = log.getIsinCode();
        ProdType pType = ProdTypeCenter.Instance.getProdType(isinCode);
        if (!pType.isEquity())
            return true;

        // BalanceMaster에 등록된 계좌만 처리
        String account = log.getAccountNumber();
        if (!balanceMaster.isValidAccount(account))
            return true;

        // 잔고 업데이트
        boolean balanceChanged = process(log);
        if (balanceChanged)
            record(log);

        // 차입 공매도 검증
        // 주문 제출이 아니면 무시
        if (!(log instanceof RequestDMALog))
            return true;

        // 신규 주문이 아니면 무시
        if (((RequestDMALog) log).getOrderKindCode() != 정정취소구분.NEW)
            return true;

        // 매수 주문이면 무시
        LongShort longShort = log.getAskBidType();
        if (longShort == LongShort.LONG)
            return true;

        int netPosition = balanceMaster.getNetPosition(isinCode, account);
        int borrowBalance = balanceMaster.getBorrowBalance(isinCode, account);

        ShortSellCode expected = ShortSellCode.일반매도;
        // 공매도로 나가야하는 주문인지 확인
        if (netPosition < 0 || borrowBalance > 0)
            expected = ShortSellCode.차입공매도;

        boolean success = expected == log.getShortSellCode();

        if (!success) {
            System.out.println("공매도 표시 오류 : 주문 제출후 순 포지션 - " + netPosition + ", " + account + " 대차 잔고 - " + borrowBalance + ", 예상 : " + expected + ", 실제 : " + log.getShortSellCode());
            System.out.print("대상 주문 : ");
            System.out.println(log);
        }

        return success;
    }

    /**
     * <h2>DMA Log로 잔고 정보 업데이트</h2>
     *
     * <p>DMA Log 정보로 잔고를 업데이트한다</p>
     * <p></p>
     *
     * <h3>주문 제출</h3>
     * <p>신규 매도 주문 제출 : 제출한 유형의 잔고 감소</p>
     * <p></p>
     *
     * <h3>주문 확인</h3>
     * <p>신규 매도 주문 거부 : 제출한 유형의 잔고 증가</p>
     * <p>IOC 매도 주문의 Auto Cancel : 제출한 유형의 잔고 증가</p>
     * <p>취소 주문 확인 : 제출한 유형의 잔고 증가</p>
     * <p></p>
     *
     * <h3>체결</h3>
     * <p>매수 체결 : 일반 잔고 증가</p>
     * <p></p>
     *
     * @param log DMA Log
     * @return 잔고 변동 여부
     */
    public boolean process(DMALog log) {
        boolean balanceChanged = false;
        String isinCode = log.getIsinCode();
        String account = log.getAccountNumber();
        LongShort longShort = log.getAskBidType();

        if (log instanceof RequestDMALog) {
            if (longShort == LongShort.LONG)
                return false;

            int amount = (int) log.getOrderQuantity();

            // 신규 매도 주문이면 제츌한 유형의 잔고 감소
            RequestDMALog requestLog = (RequestDMALog) log;
            var orderKind = requestLog.getOrderKindCode();
            var fundType = requestLog.getFundType();
            if (orderKind == 정정취소구분.NEW) {
                balanceMaster.addBalance(fundType, isinCode, account, -amount);
                balanceChanged = true;
            }

        } else if (log instanceof ReportDMALog) {
            if (longShort == LongShort.LONG)
                return false;

            ReportDMALog reportLog = (ReportDMALog) log;
            int amount = (int) reportLog.getRealAmendCancelOrderQuantity();
            var orderKind = reportLog.getOrderKindCode();
            var transactionType = log.getTransactionType();
            /*
              다음의 경우 제출한 유형의 잔고 증가
              1. 신규 매도 주문 거부
              2. IOC 매도 주문의 Auto Cancel
              3. 취소 확인
             */
            if ((orderKind == 정정취소구분.NEW && transactionType == DMALogTransactionType.REJECTED) ||
                    (transactionType == DMALogTransactionType.EXPIRED) ||
                    (orderKind == 정정취소구분.CANCEL && transactionType == DMALogTransactionType.CONFIRMED)) {
                balanceMaster.addBalance(reportLog.getFundType(), isinCode, account, amount);
                balanceChanged = true;
            }

        } else if (log instanceof TradeDMALog) {
            if (longShort == LongShort.SHORT)
                return false;

            // 매수 체결은 일반 잔고 증가
            int amount = (int) log.getOrderQuantity();
            balanceMaster.addNormalBalance(isinCode, account, amount);
            balanceChanged = true;
        }

        return balanceChanged;
    }

    public void record(DMALog log) {
        String account = log.getAccountNumber();
        String isinCode = log.getIsinCode();
        Balance accountBalance = balanceMaster.getAccountBalance(isinCode, account);
        Balance groupBalance = balanceMaster.getGroupBalance(isinCode, account);

        Document d = new Document();
        String logTypeString = log instanceof TradeDMALog ? "체결" : "주문";
        d.append(logTypeString + "시각", log.getTime())
                .append("종목코드", log.getIsinCode())
                .append("계좌번호", log.getAccountNumber())
                .append("종목명", AliasManager.Instance.getKoreanFromIsin(log.getIsinCode()))
                .append("매매구분", log.getTypeString())
                .append(logTypeString + "수량", (int) log.getOrderQuantity())
                .append("독립단위", balanceMaster.getGroupName(log.getAccountNumber()))
                .append("주문번호", log.getCurrentOrderId())
                .append("원주문번호", log.getOriginalOrderId());

        if (accountBalance != null) {
            d.append("계좌_일반잔고", accountBalance.normalBalance)
                    .append("계좌_대차잔고", accountBalance.borrowBalance)
                    .append("계좌_원대차수량", accountBalance.originalBorrowCount)
                    .append("매도가능잔고", accountBalance.normalBalance + accountBalance.borrowBalance);
        }

        if (groupBalance != null) {
            d.append("독립거래단위_일반잔고", groupBalance.normalBalance)
                    .append("독립거래단위_대차잔고", groupBalance.borrowBalance)
                    .append("독립거래단위_원대차수량", groupBalance.originalBorrowCount)
                    .append("독립거래단위_순포지션", groupBalance.getNetPosition());
        }

        documentMaster.add(d);
    }
}
