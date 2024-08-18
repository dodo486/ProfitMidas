package bulls.dmaLog;

import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.channel.hanwhaDMA.주문;
import bulls.order.enums.*;

public class RequestDMALog extends DMALog {

    private String boardId;
    private String originalOrderId;
    private String issueCode;
    private LongShort askBidType;
    private 정정취소구분 orderKindCode;
    private String accountNumber;
    private long orderQuantity;
    private double orderPrice;
    private char orderType;
    private char orderCondition;
    private String ipInformation;
    private String askTypeCode;
    private String ptSeparateCode;
    private String bookCode;
    private String purpose;
    private char askInvType;
    private char advanceNotice;

    public RequestDMALog() {
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

    @Override
    public String getPurpose() {
        return purpose;
    }

    @Override
    public String getBookCode() {
        return bookCode;
    }

    public 정정취소구분 getOrderKindCode() {
        return orderKindCode;
    }

    public FundType getFundType() {
        return FundType.getFundType((byte) askInvType);
    }

    public ShortSellCode getShortSellCode() {
        return ShortSellCode.getEnumInstance(askTypeCode);
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

        return longShortString + " " + orderKindString + " 주문 제출";
    }

    public IocFokType getIocFokType() {
        return IocFokType.getType((byte) orderCondition);
    }

    protected boolean fillDataFromPacket(String packet) {
        if (packet.length() != 주문.TOTAL_LENGTH.parser().getOffset()) {
            return false;
        }

        byte[] packetBytes = packet.getBytes();

        transactionCode = (char) 주문.TRCode.parser().parseSingleByte(packetBytes);
        boardId = 주문.장구분.parser().parseStr(packet).intern();
        orderId = 주문.주문번호.parser().parseStr(packet);
        originalOrderId = 주문.원주문번호.parser().parseStr(packet);
        issueCode = 주문.종목코드.parser().parseStr(packet).intern();
        askBidType = LongShort.getFromValue(주문.매도매수구분코드.parser().parseSingleByte(packetBytes) - '0');
        orderKindCode = 정정취소구분.valueOf(주문.정정취소구분코드.parser().parseSingleByte(packetBytes));
        if (orderKindCode == 정정취소구분.UNDEFINED)
            return false;

        accountNumber = 주문.계좌번호.parser().parseStr(packet).intern();
        orderQuantity = 주문.호가수량.parser().parseLong(packetBytes);
        orderPrice = 주문.호가가격.parser().parseDouble(packetBytes);
        orderType = (char) 주문.시장가지정가.parser().parseSingleByte(packetBytes);
        orderCondition = (char) 주문.일반IOCFOK.parser().parseSingleByte(packetBytes);
        ipInformation = 주문.IP주소.parser().parseStr(packet).intern();
        askTypeCode = 주문.매도유형코드.parser().parseStr(packet).intern();
        ptSeparateCode = 주문.PT구분코드.parser().parseStr(packet).intern();
        bookCode = 주문.북코드.parser().parseStr(packet).trim().intern();
        purpose = 주문.Purpose.parser().parseStr(packet).intern();
        askInvType = (char) 주문.매도잔고유형.parser().parseSingleByte(packetBytes);
        advanceNotice = (char) 주문.사전신고.parser().parseSingleByte(packetBytes);

        return true;
    }

    public void print() {
        System.out.println("Time : " + time);
        System.out.println("Transaction Code : " + transactionCode);
        System.out.println("Board ID : " + boardId);
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
        System.out.println("IP Information : " + ipInformation);
        System.out.println("매수매도유형코드 : " + askTypeCode);
        System.out.println("PT구분코드 : " + ptSeparateCode);
        System.out.println("북코드 : " + bookCode);
        System.out.println("Purpose : " + purpose);
        System.out.println("매도잔고유형 : " + askInvType);
        System.out.println("사전 신고 : " + advanceNotice);
    }

    @Override
    public void fillObjectNode(ObjectNode node) {
        super.fillObjectNode(node);

        node.put("transactionCode", transactionCode);
        node.put("boardId", boardId);
        node.put("originalOrderId", originalOrderId);
        node.put("issueCode", issueCode);
        node.put("askBidType", askBidType.toString());
        node.put("orderKindCode", orderKindCode.toString());
        node.put("accountNumber", accountNumber);
        node.put("orderQuantity", orderQuantity);
        node.put("orderPrice", orderPrice);
        node.put("orderType", orderType);
        node.put("orderCondition", orderCondition);
        node.put("ipInformation", ipInformation);
        node.put("askTypeCode", askTypeCode);
        node.put("ptSeparateCode", ptSeparateCode);
        node.put("bookCode", bookCode);
        node.put("purpose", purpose);
        node.put("askInvType", askInvType);
        node.put("advanceNotice", advanceNotice);
    }

    @Override
    public String toString() {
        return time + " | " + transactionCode + " | " + askBidType + " " + orderKindCode + " | " + originalOrderId + " -> " + orderId + " | " + issueCode + " | " + bookCode + " | " + accountNumber + " | " + (askBidType == LongShort.LONG ? "L" : "S") + " | " + orderQuantity + " | " + orderPrice;
    }
}

