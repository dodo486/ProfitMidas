package bulls.feed.current.data.pusan;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedPrinter;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.pusan.PusanOption_체결호가;
import bulls.log.DefaultLogger;
import bulls.order.enums.LongShort;

public class PusanOption_체결호가Feed extends Feed implements FeedPrinter {

    public PusanOption_체결호가Feed(FeedTRCode trCode, byte[] packet) {
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
        dc.updateDerivBidAskMap(ba.isinCode, ba);

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = ba.isinCode;
        info.price = getPrice();
        info.amount = getAmount();
        info.longShort = getLongShort();

        info.totalPrice = getTotalPrice();
        info.totalAmount = accVolume;
        dc.updatePriceInfo(info);
    }

    public byte[] getCodeByte() {
        return PusanOption_체결호가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    public Integer getPrice() {
        return PusanOption_체결호가.price.parser().parseInt(rawPacket);
    }

    public Integer getAmount() {
        return PusanOption_체결호가.amount.parser().parseInt(rawPacket);
    }

    public Integer getAskPrice() {
        return PusanOption_체결호가.askPrice.parser().parseInt(rawPacket);
    }

    public Integer getAskAmount() {
        return PusanOption_체결호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return PusanOption_체결호가.bidPrice.parser().parseInt(rawPacket);
    }

    public Integer getBidAmount() {
        return PusanOption_체결호가.bidAmount.parser().parseInt(rawPacket);
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
            fullBidAsk.askAmount[i] = PusanOption_체결호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = PusanOption_체결호가.askPrice.plus(i * size).parseInt(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = PusanOption_체결호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = PusanOption_체결호가.bidPrice.plus(i * size).parseInt(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public void print() {
        DefaultLogger.logger.debug("옵션체결호가 - 종목: {}, 체결가: {}, 체결 수량:{}, bid: {}/{} , ask {}/{}", getCode(), getPrice(), getAmount(), getBidPrice(), getBidAmount(), getAskPrice(), getAskAmount());
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(PusanOption_체결호가.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return PusanOption_체결호가.totalPrice.parser().parseLong(rawPacket) * 1000;
    }

    public Long getTotalAmount() {
        return PusanOption_체결호가.totalAmount.parser().parseLong(rawPacket);
    }
}
