package bulls.feed.current.data.EuroStoxx50;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.euroStoxx50.EuroStoxx50_체결호가;
import bulls.order.enums.LongShort;

public class EuroStoxx50_체결호가Feed extends Feed implements 체결Feed {

    public EuroStoxx50_체결호가Feed(FeedTRCode trCode, byte[] packet) {
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
        dc.updateDerivBidAskMap(code, ba);

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = code;
        info.price = getPrice();
        info.longShort = getLongShort();
        info.amount = getAmount();

        info.totalPrice = getTotalPrice();
        info.totalAmount = accVolume;
        dc.updatePriceInfo(info);
    }

    public byte[] getCodeByte() {
        return EuroStoxx50_체결호가.isinCode.parser().parseByte(rawPacket);
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
        return EuroStoxx50_체결호가.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public Integer getPrice() {
        return EuroStoxx50_체결호가.price.parser().parseInt(rawPacket);
    }

    @Override
    public Integer getAmount() {
        return EuroStoxx50_체결호가.amount.parser().parseInt(rawPacket);
    }

    public Integer getAskPrice() {
        return EuroStoxx50_체결호가.askPrice.parser().parseInt(rawPacket);
    }

    public Integer getAskAmount() {
        return EuroStoxx50_체결호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return EuroStoxx50_체결호가.bidPrice.parser().parseInt(rawPacket);
    }

    public Integer getBidAmount() {
        return EuroStoxx50_체결호가.bidAmount.parser().parseInt(rawPacket);
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
//        매수1단계부호	X	1
//        매수1단계우선호가가격	9	5
//        매수1단계우선호가잔량	9	6
//        1 + 5 + 6 = 12
        int size = 13;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < 5; i++) {
            fullBidAsk.askAmount[i] = EuroStoxx50_체결호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = EuroStoxx50_체결호가.askPrice.plus(i * size).parseInt(rawPacket);
        }
        for (int i = 0; i < 5; i++) {
            fullBidAsk.bidAmount[i] = EuroStoxx50_체결호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = EuroStoxx50_체결호가.bidPrice.plus(i * size).parseInt(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(EuroStoxx50_체결호가.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return EuroStoxx50_체결호가.totalPrice.parser().parseLong(rawPacket) * 1000;
    }

    public Long getTotalAmount() {
        return EuroStoxx50_체결호가.totalAmount.parser().parseLong(rawPacket);
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }
}
