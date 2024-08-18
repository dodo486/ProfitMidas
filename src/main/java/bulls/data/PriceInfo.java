package bulls.data;

import bulls.order.enums.LongShort;

public class PriceInfo {
    public String isinCode;
    public int price;
    public int amount; // 항상 양수, 방향은 longShort오로 판단해야 함
    public long totalPrice;
    public long totalAmount; //누적체결수량
    public int lpOwnAmount;
    public LongShort longShort = LongShort.UNKNOWN;
    public long feedStamp = 0; // 시세 도착 시간
    public int bidPrice;
    public int askPrice;
    public int isPriceSameWithBBO;
    public String boardId;
    public boolean isCreatedWithBidAsk = true;

    @Override
    public String toString() {
        String sb = "========= " +
                isinCode +
                " Price:" +
                price +
                " amount:" +
                amount +
                " lpOwnAmount:" +
                lpOwnAmount +
                " totalPrice:" +
                totalPrice +
                " totalAmount:" +
                totalAmount +
                " longShort:" +
                longShort +
                " askPrice:" +
                askPrice +
                " bidPrice:" +
                bidPrice;
        return sb;
    }
}

