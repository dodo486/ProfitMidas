package bulls.feed.current.data.cmtyFuture;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.cmtyFuture.CmtyFuture_체결;
import bulls.feed.current.parser.cmtyFuture.CmtyFuture_체결호가;
import bulls.order.enums.LongShort;

public class CmtyFuture_체결호가Feed extends Feed implements 체결Feed, 호가Feed, InfluxDBData {

    public CmtyFuture_체결호가Feed(FeedTRCode trCode, byte[] packet) {
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
        info.isinCode = code;
        info.totalAmount = accVolume;
        info.price = getPrice();
        info.longShort = getLongShort();
        info.amount = getAmount();
        info.totalPrice = getTotalPrice();
        info.feedStamp = arrivalStamp;

        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        ba.feedStamp = arrivalStamp;
        dc.updateDerivBidAskMap(code, ba);
        dc.updatePriceInfo(info);
    }

    public String getCode() {
        return CmtyFuture_체결호가.isinCode.parser().parseStr(rawPacket, "");
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return CmtyFuture_체결호가.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public Integer getPrice() {
        return CmtyFuture_체결호가.price.parser().parseIntWithLeadingSign(rawPacket);
    }

    @Override
    public Integer getAmount() {
        return CmtyFuture_체결호가.amount.parser().parseInt(rawPacket);
    }

    public Integer getAskPrice() {
        return CmtyFuture_체결호가.askPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getAskAmount() {
        return CmtyFuture_체결호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return CmtyFuture_체결호가.bidPrice.parser().parseIntWithLeadingSign(rawPacket);
    }

    public Integer getBidAmount() {
        return CmtyFuture_체결호가.bidAmount.parser().parseInt(rawPacket);
    }

    @Override
    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        // 가격 파싱은 8자리만 하지만 원래 길이는 9이므로 9만큼 건너뛰어야 한다.
        // 부호 길이 + 가격 길이 + 잔량 길이 = 1 + 9 + 6 = 16
        int size = 16;
        BidAskCore fullBidAsk = factory.getBidAsk(DERIV_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getCode();
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = CmtyFuture_체결호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = CmtyFuture_체결호가.askPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        for (int i = 0; i < DERIV_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = CmtyFuture_체결호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = CmtyFuture_체결호가.bidPrice.plus(i * size).parseIntWithLeadingSign(rawPacket);
        }
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    public LongShort getLongShort() {
        return LongShort.getFromByteValue(CmtyFuture_체결.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Long getTotalPrice() {
        return CmtyFuture_체결호가.totalPrice.parser().parseLong(rawPacket);
    }

    public Long getTotalAmount() {
        return CmtyFuture_체결호가.totalAmount.parser().parseLong(rawPacket);
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

}
