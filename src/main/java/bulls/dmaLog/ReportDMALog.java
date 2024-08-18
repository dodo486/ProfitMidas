package bulls.dmaLog;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import bulls.channel.hanwhaDMA.회원처리호가;
import bulls.order.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ReportDMALog extends DMALog {

    private String originalOrderId;
    private String issueCode;
    private LongShort askBidType;
    private 정정취소구분 orderKindCode;
    private String accountNumber;
    private long orderQuantity;
    private double orderPrice;
    private char orderType;
    private char orderCondition;
    private long realAmendCancelOrderQuantity;
    private char automaticCancelTypeCode;
    private String orderRejectionCode;
    private String askTypeCode;
    private String programTradingTypeCode;
    private String bookCode;
    private String purpose;
    private char askInvType;

    public ReportDMALog() {
    }

    public String getOriginalOrderId() {
        return originalOrderId;
    }

    public String getIsinCode() {
        return issueCode;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public long getOrderQuantity() {
        return orderQuantity;
    }

    public LongShort getAskBidType() {
        return askBidType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public char getTransactionCode() {
        return transactionCode;
    }

    public 정정취소구분 getOrderKindCode() {
        return orderKindCode;
    }

    public String getOrderRejectionCode() {
        return orderRejectionCode;
    }

    public long getRealAmendCancelOrderQuantity() {
        return realAmendCancelOrderQuantity;
    }

    @Override
    public String getPurpose() {
        return purpose;
    }

    @Override
    public String getBookCode() {
        return bookCode;
    }

    @Override
    public FundType getFundType() {
        return FundType.getFundType((byte) askInvType);
    }

    public ShortSellCode getShortSellCode() {
        return ShortSellCode.getEnumInstance(askTypeCode);
    }

    public char getOrderCondition() {
        return orderCondition;
    }

    public IocFokType getIocFokType() {
        return IocFokType.getType((byte) orderCondition);
    }

    @Override
    public String getTypeString() {
        String longShortString;
        if (askBidType == LongShort.LONG)
            longShortString = askBidType.getKorean();
        else {
            switch (getShortSellCode()) {
                case 일반매도 -> longShortString = "일반매도";
                case 차입공매도 -> longShortString = "차입공매도";
                case 기타매도 -> longShortString = "기타매도";
                default -> longShortString = "매도";
            }
        }

        String orderKindString;
        switch (orderKindCode) {
            case NEW -> orderKindString = "신규";
            case AMEND -> orderKindString = "정정";
            case CANCEL -> orderKindString = "취소";
            default -> orderKindString = "미상";
        }

        String transactionTypeString;
        switch (getTransactionType()) {
            case CONFIRMED -> transactionTypeString = "확인";
            case REJECTED -> transactionTypeString = "거부";
            case EXPIRED -> {
                orderKindString = "자동취소";
                transactionTypeString = "확인";
            }
            default -> {
                transactionTypeString = "알수없음";
            }
        }

        return longShortString + " " + orderKindString + " 주문 " + transactionTypeString;
    }

    protected boolean fillDataFromPacket(String packet) {
        if (packet.length() != 141) {
            return false;
        }

        byte[] packetBytes = packet.getBytes();

        transactionCode = (char) 회원처리호가.TR구분.parser().parseSingleByte(packetBytes);
        orderId = 회원처리호가.주문번호.parser().parseStr(packet);
        originalOrderId = 회원처리호가.원주문번호.parser().parseStr(packet);
        issueCode = 회원처리호가.종목코드.parser().parseStr(packet).intern();
        askBidType = LongShort.getFromValue(회원처리호가.매도매수구분코드.parser().parseSingleByte(packetBytes) - '0');
        orderKindCode = 정정취소구분.valueOf(회원처리호가.정정취소구분코드.parser().parseSingleByte(packetBytes));
        if (orderKindCode == 정정취소구분.UNDEFINED)
            return false;

        accountNumber = 회원처리호가.계좌번호.parser().parseStr(packet).intern();
        orderQuantity = 회원처리호가.호가수량.parser().parseLong(packetBytes);
        orderPrice = 회원처리호가.호가가격.parser().parseDouble(packetBytes);
        orderType = (char) 회원처리호가.시장가지정가.parser().parseSingleByte(packetBytes);
        orderCondition = (char) 회원처리호가.일반IOCFOK.parser().parseSingleByte(packetBytes);
        realAmendCancelOrderQuantity = 회원처리호가.실정정취소호가수량.parser().parseLong(packetBytes);
        automaticCancelTypeCode = (char) 회원처리호가.자동취소타입.parser().parseSingleByte(packetBytes);
        orderRejectionCode = 회원처리호가.거부코드.parser().parseStr(packet).intern();
        askTypeCode = 회원처리호가.매도유형코드.parser().parseStr(packet).intern();
        programTradingTypeCode = 회원처리호가.PT구분코드.parser().parseStr(packet).intern();
        bookCode = 회원처리호가.북코드.parser().parseStr(packet).trim().intern();
        purpose = 회원처리호가.Purpose.parser().parseStr(packet).intern();
        askInvType = (char) 회원처리호가.매도잔고유형.parser().parseSingleByte(packetBytes);

        return true;
    }

    public void print() {
        System.out.println("Time : " + time);
        System.out.println("Transaction Code : " + transactionCode);
        System.out.println("Order ID : " + orderId);
        System.out.println("Original Order ID : " + originalOrderId);
        System.out.println("Issue Code : " + issueCode);
        System.out.println("Ask/Bid Type : " + askBidType);
        System.out.println("Order Kind Code : " + orderKindCode);
        System.out.println("Account Number : " + accountNumber);
        System.out.println("Order Quantity : " + orderQuantity);
        System.out.println("Order Price : " + orderPrice);
        System.out.println("Order Type : " + orderType);
        System.out.println("Order Condition : " + orderCondition);
        System.out.println("Real Amend/Cancel Order Quantity : " + realAmendCancelOrderQuantity);
        System.out.println("Automatic Cancel Type Code : " + automaticCancelTypeCode);
        System.out.println("Order Rejection Code : " + orderRejectionCode);
        System.out.println("Ask Type Code : " + askTypeCode);
        System.out.println("Program Trading Type Code : " + programTradingTypeCode);
        System.out.println("북코드 : " + bookCode);
        System.out.println("Purpose : " + purpose);
        System.out.println("매도잔고유형 : " + askInvType);
    }

    @Override
    public void fillObjectNode(ObjectNode node) {
        super.fillObjectNode(node);

        node.put("transactionCode", transactionCode);
        node.put("originalOrderId", originalOrderId);
        node.put("issueCode", issueCode);
        node.put("askBidType", askBidType.toString());
        node.put("orderKindCode", orderKindCode.toString());
        node.put("accountNumber", accountNumber);
        node.put("orderQuantity", orderQuantity);
        node.put("orderPrice", orderPrice);
        node.put("orderType", orderType);
        node.put("orderCondition", orderCondition);
        node.put("realAmendCancelOrderQuantity", realAmendCancelOrderQuantity);
        node.put("automaticCancelTypeCode", automaticCancelTypeCode);
        node.put("orderRejectionCode", orderRejectionCode);
        node.put("askTypeCode", askTypeCode);
        node.put("programTradingTypeCode", programTradingTypeCode);
        node.put("bookCode", bookCode);
        node.put("purpose", purpose);
        node.put("askInvType", askInvType);
    }

    public Point toPoint(LocalDate date) {
        LocalDateTime t = LocalDateTime.of(date, getTime());
        String account = getAccountNumber().trim();
        String bookCode = getBookCode().trim();
        String isinCode = getIsinCode().trim();
        String purpose = getPurpose().trim();
        String longShort = getAskBidType().toString();
        double price = getOrderPrice();
        int requestAmount = (int) getOrderQuantity();
        int realAmount = (int) getRealAmendCancelOrderQuantity();

        // 헤지로 나가는 BF 주문 or C OMS 주문은 purpose가 비어있음
        if (purpose.length() == 0)
            purpose = "BLK";

        return Point.measurement("fepReportLog")
                .addTag("account", account)
                .addTag("bookCode", bookCode)
                .addTag("isinCode", isinCode)
                .addTag("purpose", purpose)
                .addField("longShort", longShort)
                .addField("price", price)
                .addField("requestAmount", requestAmount)
                .addField("realAmount", realAmount)
                .time(t.atZone(ZoneId.systemDefault()).toInstant(), WritePrecision.NS);
    }

    @Override
    public String toString() {
        return time + " | " + transactionCode + " | " + askBidType + " " + orderKindCode + " | " + originalOrderId + " -> " + orderId + " | " + issueCode + " | " + bookCode + " | " + accountNumber + " | " + (askBidType == LongShort.LONG ? "L" : "S") + " | " + orderQuantity + "(" + realAmendCancelOrderQuantity + ") | " + orderPrice;
    }
}
