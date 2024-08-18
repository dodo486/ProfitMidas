package bulls.feed.current.data.pusan;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedPrinter;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.pusan.PusanFuture_체결호가;
import bulls.log.DefaultLogger;
import bulls.order.enums.LongShort;

public class PusanFuture_체결호가Feed extends Feed implements FeedPrinter {

    public PusanFuture_체결호가Feed(FeedTRCode trCode, byte[] packet) {
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

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = ba.isinCode;
        info.price = getPrice();
        info.amount = getAmount();
        info.longShort = getLongShort();
        info.totalAmount = accVolume;
        info.totalPrice = getTotalPrice();
        ba.priceInfo = info;
        dc.updateDerivBidAskMap(ba.isinCode, ba);
        dc.updatePriceInfo(info);
    }

    public byte[] getCodeByte() {
        return PusanFuture_체결호가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    public Integer getPrice() {
        return PusanFuture_체결호가.price.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAmount() {
        return PusanFuture_체결호가.amount.parser().parseInt(rawPacket);
    }

    public Integer getAskPrice() {
        return PusanFuture_체결호가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return PusanFuture_체결호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return PusanFuture_체결호가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return PusanFuture_체결호가.bidAmount.parser().parseInt(rawPacket);
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
            fullBidAsk.askAmount[i] = PusanFuture_체결호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = PusanFuture_체결호가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = PusanFuture_체결호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = PusanFuture_체결호가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public void print() {
        DefaultLogger.logger.debug("{} : [{}]", new String(rawPacket).length(), new String(rawPacket));
        DefaultLogger.logger.debug("선물체결호가 - 종목: {}, 체결가: {}, 체결 수량:{}, bid: {}/{} , ask {}/{}", getCode(), getPrice(), getAmount(), getBidPrice(), getBidAmount(), getAskPrice(), getAskAmount());
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(PusanFuture_체결호가.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return PusanFuture_체결호가.totalPrice.parser().parseLong(rawPacket) * 1000;
    }

    public Long getTotalAmount() {
        return PusanFuture_체결호가.totalAmount.parser().parseLong(rawPacket);
    }
}
