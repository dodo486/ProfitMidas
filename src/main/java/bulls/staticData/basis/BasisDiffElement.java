package bulls.staticData.basis;

import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.staticData.AliasManager;
import bulls.staticData.FuturesInfo;
import bulls.staticData.FuturesInfoCenter;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import bulls.tool.util.MsgUtil;
import org.bson.Document;

import java.time.LocalDate;

public final class BasisDiffElement {
    public final String bookCode;
    public final String futuresIsinCode;
    public final LocalDate date;
    public final double amount;

    public final LocalDate lastWorkingDay;
    public final DerivativesUnderlyingType dut;
    public final double basisDiff;
    public final double delta;

    public BasisDiffElement(String bookCode, String futuresIsinCode, LocalDate date, double amount) {
        this.bookCode = bookCode;
        this.futuresIsinCode = futuresIsinCode;
        this.date = date;
        this.amount = amount;

        this.lastWorkingDay = TimeCenter.getLastWorkingDayOf(date);

        FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(futuresIsinCode);
        if (info == null)
            this.dut = DerivativesUnderlyingType.UNKNOWN;
        else
            this.dut = info.기초자산ID;

        this.basisDiff = getBasisDiff(futuresIsinCode, dut);
        this.delta = getDelta(this.basisDiff);
    }

    private double getDelta(double basisDiff) {
        if (basisDiff == 0)
            return 0;

        FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(futuresIsinCode);
        if (info == null)
            return 0;

        return amount * basisDiff * info.multiplier;
    }

    private double getBasisDiff(String futuresIsinCode, DerivativesUnderlyingType dut) {
        Double beforeFuturesPrice = getClosingPriceFutures(lastWorkingDay, futuresIsinCode);
        Double beforeIndexPrice = getClosingPriceIndex(lastWorkingDay, dut);
        Double afterFuturesPrice = getClosingPriceFutures(date, futuresIsinCode);
        Double afterIndexPrice = getClosingPriceIndex(date, dut);

        String korean = AliasManager.Instance.getKoreanFromIsin(futuresIsinCode);
        if (beforeFuturesPrice == null || beforeIndexPrice == null || afterFuturesPrice == null || afterIndexPrice == null) {
            System.out.println(
                    MsgUtil.getFormattedString("{} ({}) - {} 간의 베이시스 차이를 구하지 못했습니다. 전일 선물 가격:{}, 전일 지수 가격:{}, 당일 선물 가격:{}, 당일 지수 가격:{}",
                            korean, futuresIsinCode, dut.getKorean(), beforeFuturesPrice, beforeIndexPrice, afterFuturesPrice, afterIndexPrice)
            );
            return 0;
        }

        return (afterFuturesPrice - afterIndexPrice) - (beforeFuturesPrice - beforeIndexPrice);
    }

    private Double getClosingPriceIndex(LocalDate date, DerivativesUnderlyingType dut) {
        Document query = new Document();
        query.append("indexIsin", dut.getUnderlyingIsinCode());
        query.append("date", TimeCenter.getLocalDateAsDateType(date));
        Document doc = DBCenter.Instance.findIterable(MongoDBDBName.CLOSING_PRICE, MongoDBCollectionName.INDEX_CLOSING, query).first();
        if (doc == null)
            return null;

        Integer price = doc.getInteger("price");
        if (price == null)
            return null;

        return price * 0.01;
    }

    private Double getClosingPriceFutures(LocalDate date, String futuresIsinCode) {
        Document query = new Document();
        query.append("isinCode", futuresIsinCode);
        query.append("date", TimeCenter.getLocalDateAsDateType(date));
        Document doc = DBCenter.Instance.findIterable(MongoDBDBName.CLOSING_PRICE, MongoDBCollectionName.FUTURES_CLOSING, query).first();
        if (doc == null)
            return null;

        Integer price = doc.getInteger("price");
        if (price == null)
            return null;

        FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(futuresIsinCode);
        if (info == null) {
            System.out.println("선물 종가 주의 : " + futuresIsinCode + " FuturesInfo 없음");
            return (double) price;
        }

        return (double) price / info.priceDivider;
    }

    public String toMarkdownRow() {
        String korean = AliasManager.Instance.getKoreanFromIsin(futuresIsinCode);
        String sb = "|" + bookCode +
                "|" + korean +
                "|" + futuresIsinCode +
                "|" + String.format("%.2f", amount) +
//        sb.append("|").append(String.format("%.2f", basisDiff));
//        sb.append("|").append(String.format("%.0f", delta));
                "|\n";

        return sb;
    }

    @Override
    public String toString() {
        return "BasisDiffElement{" +
                "bookCode='" + bookCode + '\'' +
                ", futuresIsinCode='" + futuresIsinCode + '\'' +
                ", dut=" + dut +
                ", amount=" + amount +
                ", date=" + date +
                ", lastWorkingDay=" + lastWorkingDay +
                ", basisDiff=" + basisDiff +
                ", delta=" + delta +
                '}';
    }
}
