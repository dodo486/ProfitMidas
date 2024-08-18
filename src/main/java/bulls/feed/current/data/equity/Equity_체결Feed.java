package bulls.feed.current.data.equity;

import bulls.data.PriceInfo;
import bulls.db.influxdb.InfluxDBData;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.LPOwnAmountProvider;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.equity.Equity_체결;
import bulls.feed.dc.filter.G2AccVolumeEquityContractValidator;
import bulls.order.enums.LongShort;

public class Equity_체결Feed extends Feed implements 체결Feed, InfluxDBData, LPOwnAmountProvider {

    public Equity_체결Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        String isinCode = getIsinCode();
        String boardId = getBoardId();
        long volume = getTotalAmount();

        PriceInfo info = new PriceInfo();
        info.feedStamp = arrivalStamp;
        info.isinCode = isinCode;
        info.price = getPrice();
        info.longShort = getLongShort();
        info.amount = getAmount();
        info.totalAmount = volume;
        info.lpOwnAmount = getLPOwnAmount();
        info.bidPrice = getBidPrice();
        info.askPrice = getAskPrice();
        info.isPriceSameWithBBO = isPriceSameWithBBO();
        info.boardId = boardId;
        info.totalPrice = getTotalPrice();
        info.isCreatedWithBidAsk = false;

        // G3, G4 는 장종료 이후이므로 관심없음
        // G2 (장개시전시간외 종가) 누적 체결 관리를 위해 분기한다.
        switch (boardId) {
            case "G1" -> dc.handleEquityContract(info);
            case "G2" -> G2AccVolumeEquityContractValidator.Instance.updateG2(isinCode, volume);
        }

        dc.updatePriceInfo(info);
    }

    public byte[] getCodeByte() {
        return Equity_체결.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    public byte[] getIsinByte() {
        return Equity_체결.isinCode.parser().parseByte(rawPacket);
    }

    public String getIsinCode() {
        return new String(getIsinByte());
    }

    @Override
    public String getRepresentingCode() {
        return getIsinCode();
    }

    @Override
    public String getBoardId() {
        return Equity_체결.boardId.parser().parseStr(rawPacket, "");
    }

    @Override
    public Integer getPrice() {
        return Equity_체결.price.parser().parseInt(rawPacket);
    }

    @Override
    public Integer getAmount() {
        return Equity_체결.amount.parser().parseInt(rawPacket);
    }

    public Integer isPriceSameWithBBO() {
        return Equity_체결.isPriceSameWithFirstBidAsk.parser().parseInt(rawPacket);
    }

    public int getLPOwnAmount() {
        return Equity_체결.lpOwnAmount.parser().parseIntWithLeadingSign(rawPacket);
    }

    public LongShort getLongShort() {
        //1:매도 2:매수
        return LongShort.getFromByteValue(Equity_체결.buySellSign.parser().parseSingleByte(rawPacket));
    }

    public Integer getBidPrice() {
        return Equity_체결.bidPrice.parser().parseInt(rawPacket);
    }

    public Integer getAskPrice() {
        return Equity_체결.askPrice.parser().parseInt(rawPacket);
    }

    public Integer getKrxTime() {
        return Equity_체결.krxTime.parser().parseInt(rawPacket);
    }

    public Long getTotalPrice() {
        return Equity_체결.totalPrice.parser().parseLong(rawPacket);
    }

    public Long getTotalAmount() {
        return Equity_체결.totalAmount.parser().parseLong(rawPacket);
    }

    @Override
    public long getArrivalStamp() {
        return arrivalStamp;
    }

}
