package bulls.feed.current.data.equity;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.BidAskFactory;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.equity.Equity_호가;

/**
 * ETF/ELW 는 B7011 로 들어오고 일반 주식은 B6011/6012로 들어옴
 * 1호가 가격/수량은 두 TR이 동일하여 하나의 Feed 로 처리중 ( EquityBidAskDC constructor 의 trListToAccept 참고)
 * 향후 만일 totalBid/Ask 수량과 같이 두 TR의 offset/Length 가 각각 다른 데이터를 써야 된다면 이 클래스는 나뉘어져 구현되어야함
 */
public class Equity_호가Feed extends Feed implements 호가Feed, InfluxDBData {

    public Equity_호가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        String isinCode = getIsinCode();
        long volume = getVolume();

        BidAskCore ba = getFullBidAsk(dc.getBidAskFactory());
        ba.isinCode = isinCode;
        ba.accVolume = volume;
        ba.feedStamp = arrivalStamp;
        ba.expPrice = getExpPrice();
        ba.expAmt = getExpAmount();
        dc.updateEquityBidAskMap(ba.isinCode, ba);
    }

    public byte[] getCodeByte() {
        return Equity_호가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    public byte[] getIsinByte() {
        return Equity_호가.isinCode.parser().parseByte(rawPacket);
    }

    public String getIsinCode() {
        return new String(getIsinByte());
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public String getBoardId() {
        return Equity_호가.boardId.parser().parseStr(rawPacket, "");
    }

    public BidAskCore getFullBidAsk(BidAskFactory factory) {
        int size = 42;
        BidAskCore fullBidAsk = factory.getBidAsk(EQUITY_MAX_BIDASK_DEPTH);
        fullBidAsk.isinCode = getIsinCode();
        for (int i = 0; i < EQUITY_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.askAmount[i] = Equity_호가.askAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.askAmount[i] == 0)
                break;
            fullBidAsk.askPrice[i] = Equity_호가.askPrice.plus(i * size).parseInt(rawPacket);
        }
        for (int i = 0; i < EQUITY_MAX_BIDASK_DEPTH; i++) {
            fullBidAsk.bidAmount[i] = Equity_호가.bidAmount.plus(i * size).parseInt(rawPacket);
            if (fullBidAsk.bidAmount[i] == 0)
                break;
            fullBidAsk.bidPrice[i] = Equity_호가.bidPrice.plus(i * size).parseInt(rawPacket);
        }
        fullBidAsk.accVolume = getVolume();
        fullBidAsk.updateBidAskState();
        return fullBidAsk;
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

    public Integer getAskPrice() {
        return Equity_호가.askPrice.parser().parseInt(rawPacket);
    }

    public Integer getAskAmount() {
        return Equity_호가.askAmount.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return Equity_호가.bidPrice.parser().parseInt(rawPacket);
    }

    public Integer getExpPrice() {
        return Equity_호가.expPrice.parser().parseInt(rawPacket);
    }

    public Integer getExpAmount() {
        return Equity_호가.expAmount.parser().parseInt(rawPacket);
    }

    public Integer getVolume() {
        return Equity_호가.volume.parser().parseInt(rawPacket);
    }

}