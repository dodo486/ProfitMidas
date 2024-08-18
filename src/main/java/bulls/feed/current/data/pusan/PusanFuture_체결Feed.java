package bulls.feed.current.data.pusan;

import bulls.data.PriceInfo;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedPrinter;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.pusan.PusanFuture_체결;
import bulls.log.DefaultLogger;
import bulls.order.enums.LongShort;

import java.io.UnsupportedEncodingException;

public class PusanFuture_체결Feed extends Feed implements FeedPrinter {

    public PusanFuture_체결Feed(FeedTRCode trCode, byte[] packet) {
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

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = code;
        info.price = getPrice();
        info.amount = getAmount();
        info.longShort = getLongShort();
        info.isCreatedWithBidAsk = false;

        info.totalPrice = getTotalPrice();
        info.totalAmount = accVolume;
        dc.handleDerivContract(info);
        dc.updatePriceInfo(info);
    }

    public byte[] getCodeByte() {
        return PusanFuture_체결.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        try {
            return PusanFuture_체결.isinCode.parser().parseStr(rawPacket);
        } catch (UnsupportedEncodingException e) {
            DefaultLogger.logger.error("error found", e);
        }

        return new String(getCodeByte());
    }

    public Integer getPrice() {
        return PusanFuture_체결.price.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAmount() {
        return PusanFuture_체결.amount.parser().parseInt(rawPacket);
    }

    public void print() {
        DefaultLogger.logger.debug("{} : [{}]", new String(rawPacket).length(), new String(rawPacket));
        DefaultLogger.logger.debug("선물체결 - 종목: {}, 체결가: {}, 체결 수량:{}", getCode(), getPrice(), getAmount());
    }


    public LongShort getLongShort() {
        return LongShort.getFromByteValue(PusanFuture_체결.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return PusanFuture_체결.totalPrice.parser().parseLong(rawPacket) * 1000;
    }

    public Long getTotalAmount() {
        return PusanFuture_체결.totalAmount.parser().parseLong(rawPacket);
    }
}
