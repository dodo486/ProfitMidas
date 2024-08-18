package bulls.feed.current.data.kosdaqOption;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.kosdaqOption.KQOption_체결호가;
import bulls.order.enums.LongShort;

public class KQOption_체결호가Feed extends Feed implements 체결Feed, 호가Feed, InfluxDBData {

    public KQOption_체결호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {

        String code = getCode();
        long accVolume = getTotalAmount();
        if (dc.isOutDatedAccVolume(code, accVolume)) {
            // 지각 패킷은 아무것도 하지 않는다.
            return;
        }

        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        ba.feedStamp = arrivalStamp;
        dc.updateDerivBidAskMap(code, ba);

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = code;
        info.price = getPrice();
        info.amount = getAmount();
        info.longShort = getLongShort();

        info.totalPrice = getTotalPrice();
        info.totalAmount = getTotalAmount();
        dc.updatePriceInfo(info);
    }

    public byte[] getCodeByte() {
        return KQOption_체결호가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return KQOption_체결호가.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public Integer getPrice() {
        return KQOption_체결호가.price.parser().parseInt(rawPacket);
    }

    @Override
    public Integer getAmount() {
        return KQOption_체결호가.amount.parser().parseInt(rawPacket);
    }

    public Integer getAskPrice() {
        return KQOption_체결호가.askPrice.parser().parseInt(rawPacket);
    }

    public Integer getAskAmount() {
        return KQOption_체결호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return KQOption_체결호가.bidPrice.parser().parseInt(rawPacket);
    }

    public Integer getBidAmount() {
        return KQOption_체결호가.bidAmount.parser().parseInt(rawPacket);
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
//        매수1단계부호	X	1
//        매수1단계우선호가가격	9	5
//        매수1단계우선호가잔량	9	6
//        1 + 5 + 6 = 12
        int size = 12;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = KQOption_체결호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = KQOption_체결호가.askPrice.plus(i * size).parseInt(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = KQOption_체결호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = KQOption_체결호가.bidPrice.plus(i * size).parseInt(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(KQOption_체결호가.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return KQOption_체결호가.totalPrice.parser().parseLong(rawPacket) * 1000;
    }

    public Long getTotalAmount() {
        return KQOption_체결호가.totalAmount.parser().parseLong(rawPacket);
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

}
