package bulls.dmaLog;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import bulls.analysis.contractType.ContractType;
import bulls.analysis.contractType.ContractTypeChecker;
import bulls.channel.hanwhaDMA.체결;
import bulls.dateTime.TimeCenter;
import bulls.dmaLog.transactiontracker.TransactionTracker;
import bulls.order.enums.FundType;
import bulls.order.enums.LongShort;
import bulls.order.enums.ShortSellCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class TradeDMALog extends DMALog {

    private String issueCode;
    private String accountNumber;
    private long krxExecutionNumber;
    private double krxExecutionPrice;
    private long krxExecutionQuantity;
    private String sessionId;
    private LocalTime krxExecutionTime;
    private double nearbyMonthTradingPrice;
    private double futureMonthTradingPrice;
    private LongShort askBidType;
    private String bookCode;
    private String purpose;
    private char askInvType;

    private ContractType contractType = ContractType.UNKNOWN;

    public TradeDMALog() {
    }

    public String getOriginalOrderId() {
        return FEPConstantValue.ORDER_ID_ZERO;
    }

    public String getIsinCode() {
        return issueCode;
    }

    public double getOrderPrice() {
        return krxExecutionPrice;
    }

    public long getOrderQuantity() {
        return krxExecutionQuantity;
    }

    public LongShort getAskBidType() {
        return askBidType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBookCode() {
        return bookCode;
    }

    public LocalTime getKrxExecutionTime() {
        return krxExecutionTime;
    }

    public long getKrxExecutionNumber() {
        return krxExecutionNumber;
    }

    @Override
    public String getPurpose() {
        return purpose;
    }

    @Override
    public FundType getFundType() {
        return FundType.getFundType((byte) askInvType);
    }

    @Override
    public ShortSellCode getShortSellCode() {
        return ShortSellCode.NA;
    }

    public ContractType updateAndGetContractType() {
        if (contractType == ContractType.UNKNOWN && TransactionTracker.Instance.isActive())
            contractType = ContractTypeChecker.getType(this);
        return contractType;
    }

    protected boolean fillDataFromPacket(String packet) {
        if (packet.length() != 체결.TOTAL_LENGTH.parser().getOffset()) {
            return false;
        }

        byte[] packetBytes = packet.getBytes();

        transactionCode = (char) 체결.TR구분.parser().parseSingleByte(packetBytes);
        orderId = 체결.주문ID.parser().parseStr(packet);
        issueCode = 체결.종목코드.parser().parseStr(packet).intern();
        accountNumber = 체결.계좌번호.parser().parseStr(packet).intern();
        krxExecutionNumber = 체결.체결번호.parser().parseLong(packetBytes);
        krxExecutionPrice = 체결.체결가격.parser().parseDouble(packetBytes);
        krxExecutionQuantity = 체결.체결수량.parser().parseLong(packetBytes);
        sessionId = 체결.장구분.parser().parseStr(packet).intern();
        krxExecutionTime = DMALogUtil.convertKRXTime(체결.체결시각.parser().parseStr(packet));
        nearbyMonthTradingPrice = 체결.근월물체결가격.parser().parseDouble(packetBytes);
        futureMonthTradingPrice = 체결.원월물체결가격.parser().parseDouble(packetBytes);
        askBidType = LongShort.getFromValue(체결.매도매수구분코드.parser().parseSingleByte(packetBytes) - '0');
        bookCode = 체결.북코드.parser().parseStr(packet).trim().intern();
        purpose = 체결.Purpose.parser().parseStr(packet).intern();
        askInvType = (char) 체결.매도잔고유형.parser().parseSingleByte(packetBytes);

        return true;
    }

    public void print() {
        System.out.println("Time : " + time);
        System.out.println("Transaction Code : " + transactionCode);
        System.out.println("Order ID : " + orderId);
        System.out.println("Issue Code : " + issueCode);
        System.out.println("Account Number : " + accountNumber);
        System.out.println("KRX Execution Number : " + krxExecutionNumber);
        System.out.println("KRX Execution Price : " + krxExecutionPrice);
        System.out.println("KRX Execution Quantity : " + krxExecutionQuantity);
        System.out.println("Session ID : " + sessionId);
        System.out.println("KRX Execution Time : " + krxExecutionTime);
        System.out.println("The Nearby Month Trading Price : " + nearbyMonthTradingPrice);
        System.out.println("The Future Month Trading Price : " + futureMonthTradingPrice);
        System.out.println("Ask/Bid Type Code : " + askBidType);
        System.out.println("북코드 : " + bookCode);
        System.out.println("Purpose : " + purpose);
        System.out.println("매도잔고유형 : " + askInvType);
    }

    public long getKrxExecutionQuantity() {
        return krxExecutionQuantity;
    }

    @Override
    public String getTypeString() {
        return askBidType.getKorean() + "체결";
    }

    @Override
    public void fillObjectNode(ObjectNode node) {
        super.fillObjectNode(node);
        node.put("transactionCode", transactionCode);
        node.put("issueCode", issueCode);
        node.put("accountNumber", accountNumber);
        node.put("krxExecutionNumber", krxExecutionNumber);
        node.put("krxExecutionPrice", krxExecutionPrice);
        node.put("krxExecutionQuantity", krxExecutionQuantity);
        node.put("sessionId", sessionId);
        node.put("krxExecutionTime", krxExecutionTime.toString());
        node.put("nearbyMonthTradingPrice", nearbyMonthTradingPrice);
        node.put("futureMonthTradingPrice", futureMonthTradingPrice);
        node.put("askBidType", askBidType.toString());
        node.put("bookCode", bookCode);
        node.put("purpose", purpose);
        node.put("askInvType", askInvType);
        node.put("contractType", updateAndGetContractType().name());
    }

    @Override
    public String toString() {
        String orderTypeString = "FILL       ";
        String originalorderIdFillString = FEPConstantValue.ORDER_ID_NEW;

        return time + " | " + transactionCode + " | " + orderTypeString + " | " + originalorderIdFillString + " -> " + orderId + " | " + issueCode + " | " + bookCode + " | " + accountNumber + " | " + (askBidType == LongShort.LONG ? "L" : "S") + " | " + krxExecutionQuantity + " | " + krxExecutionPrice + " | " + krxExecutionTime;
    }

    public Point toPoint(LocalDate date) {
        LocalDateTime t = LocalDateTime.of(date, getTime());
        String account = getAccountNumber().trim();
        String bookCode = getBookCode().trim();
        String isinCode = getIsinCode().trim();
        String purpose = getPurpose().trim();
        String longShort = getAskBidType().toString();
        double price = getOrderPrice();
        int amount = (int) getKrxExecutionQuantity();

        // 헤지로 나가는 BF 주문 or C OMS 주문은 purpose가 비어있음
        if (purpose.length() == 0)
            purpose = "BLK";

        return Point.measurement("fepTradeLog")
                .addTag("account", account)
                .addTag("bookCode", bookCode)
                .addTag("isinCode", isinCode)
                .addTag("purpose", purpose)
                .addField("longShort", longShort)
                .addField("price", price)
                .addField("amount", amount)
                .addField("money", price * amount)
                .time(t.atZone(ZoneId.systemDefault()).toInstant(), WritePrecision.NS);
    }

    public Point toPoint() {
        return toPoint(TimeCenter.Instance.today);
    }
}