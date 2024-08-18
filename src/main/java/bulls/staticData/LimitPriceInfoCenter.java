package bulls.staticData;

import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.order.enums.LongShort;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import com.mongodb.client.MongoCursor;
import org.apache.commons.math3.util.FastMath;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum LimitPriceInfoCenter implements EarlyInitialize {
    Instance;

    private final ConcurrentMap<String, LimitPriceInfo> limitPriceInfoMap;
    private final LimitPriceInfo nullLimitPriceInfo;

    LimitPriceInfoCenter() {
        limitPriceInfoMap = new ConcurrentHashMap<>();
        nullLimitPriceInfo = new LimitPriceInfo("", Integer.MAX_VALUE, Integer.MIN_VALUE);

        EquityInfoCenter.Instance.getAllEquityInfo().forEach(info -> {
            LimitPriceInfo limitPriceInfo;
            if (info.상한가 == 0 && info.하한가 == 0)    // ELW의 경우 상한가, 하한가가 모두 0인 경우가 있음. 이럴 때는 무제한으로 설정해준다.
                limitPriceInfo = new LimitPriceInfo(info.isinCode, Integer.MAX_VALUE, 0);
            else
                limitPriceInfo = new LimitPriceInfo(info.isinCode, (int) info.상한가, (int) info.하한가);

            limitPriceInfoMap.put(info.isinCode, limitPriceInfo);
        });

        // Batch 정보는 double로 관리되므로 지수나 섹터는 priceDivider를 곱해줘야 함
        FuturesInfoCenter.Instance.getAllFuturesInfo().forEach(info -> {
            ProdType type = ProdTypeCenter.Instance.getProdType(info.isinCode);
            if (type.isFloatingPointDeriv())
                limitPriceInfoMap.put(info.isinCode, new LimitPriceInfo(info.isinCode, (int) FastMath.round(info.가격제한1단계상한가 * info.priceDivider), (int) FastMath.round(info.가격제한1단계하한가 * info.priceDivider)));
            else
                limitPriceInfoMap.put(info.isinCode, new LimitPriceInfo(info.isinCode, (int) FastMath.round(info.가격제한1단계상한가), (int) FastMath.round(info.가격제한1단계하한가)));
        });

        // DB에는 당일 장중 상하한가 업데이트 정보가 있으므로 DB 정보로 업데이트해준다.
        MongoCursor<Document> cursor = DBCenter.Instance.find(MongoDBDBName.PRICE, MongoDBCollectionName.LIVE_LIMIT_PRICE, Document.class);

        while (cursor.hasNext()) {
            Document doc = cursor.next();

            String isinCode = doc.getString("isinCode");
            int upperLimitPrice = doc.getInteger("upperLimitPrice");
            int lowerLimitPrice = doc.getInteger("lowerLimitPrice");

            if (limitPriceInfoMap.containsKey(isinCode)) {
                limitPriceInfoMap.get(isinCode).updateLowerLimitPrice(lowerLimitPrice).updateUpperLimitPrice(upperLimitPrice);
            } else {
                setLimitPriceInfo(isinCode, new LimitPriceInfo(isinCode, upperLimitPrice, lowerLimitPrice));
            }
        }

        cursor.close();
    }

    public void setLimitPriceInfo(String isinCode, LimitPriceInfo info) {
        limitPriceInfoMap.put(isinCode, info);
    }

    public void updateLimitPriceInfo(String isinCode, int upperLimitPrice, int lowerLimitPrice) {
        if (upperLimitPrice < lowerLimitPrice) {
            int temp;
            temp = upperLimitPrice;
            upperLimitPrice = lowerLimitPrice;
            lowerLimitPrice = temp;
        }

        LimitPriceInfo info;

        if (limitPriceInfoMap.containsKey(isinCode)) {
            info = limitPriceInfoMap.get(isinCode).updateLowerLimitPrice(lowerLimitPrice).updateUpperLimitPrice(upperLimitPrice);
        } else {
            info = new LimitPriceInfo(isinCode, upperLimitPrice, lowerLimitPrice);
            setLimitPriceInfo(isinCode, info);
        }

        if (TempConf.LIMIT_PRICE_TO_DB) {
            info.updateToDB();
        }
    }

    @NotNull
    private LimitPriceInfo getLimitPriceInfo(String isinCode) {
        return limitPriceInfoMap.computeIfAbsent(isinCode,
                code -> {
                    ProdType pType = ProdTypeCenter.Instance.getProdType(code);
                    LimitPriceInfo limitPriceInfo;

                    if (pType.isEquity()) {
                        EquityInfo info = EquityInfoCenter.Instance.getEquityInfo(isinCode);

                        if (info == null)
                            limitPriceInfo = nullLimitPriceInfo;
                        else if (info.상한가 == 0 && info.하한가 == 0)    // ELW의 경우 상한가, 하한가가 모두 0인 경우가 있음. 이럴 때는 무제한으로 설정해준다.
                            limitPriceInfo = new LimitPriceInfo(isinCode, Integer.MAX_VALUE, 0);
                        else
                            limitPriceInfo = new LimitPriceInfo(isinCode, (int) info.상한가, (int) info.하한가);
                    } else {
                        FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(isinCode);

                        if (info == null)
                            limitPriceInfo = nullLimitPriceInfo;
                        else {
                            ProdType type = ProdTypeCenter.Instance.getProdType(info.isinCode);

                            if (type.isFloatingPointDeriv())
                                limitPriceInfo = new LimitPriceInfo(isinCode, (int) FastMath.round(info.가격제한1단계상한가 * 100), (int) FastMath.round(info.가격제한1단계하한가 * 100));
                            else
                                limitPriceInfo = new LimitPriceInfo(isinCode, (int) FastMath.round(info.가격제한1단계상한가), (int) FastMath.round(info.가격제한1단계하한가));
                        }
                    }

                    if (TempConf.LIMIT_PRICE_TO_DB && limitPriceInfo != nullLimitPriceInfo)
                        limitPriceInfo.updateToDB();

                    return limitPriceInfo;
                });
    }

    public int getUpperLimitPrice(String isinCode) {
        return getLimitPriceInfo(isinCode).getUpperLimitPrice();
    }

    public int getLowerLimitPrice(String isinCode) {
        return getLimitPriceInfo(isinCode).getLowerLimitPrice();
    }

    public boolean isNullLimitPriceInfo(LimitPriceInfo info) {
        return info == nullLimitPriceInfo;
    }

    public boolean isValidPrice(String isinCode, LongShort longShort, int price) {
        LimitPriceInfo info = getLimitPriceInfo(isinCode);

        // 상하한가 정보가 없는 경우에는 무조건 true
        if (info == nullLimitPriceInfo || TempConf.IGNORE_LIMIT_PRICE)
            return true;

        return (info.getLowerLimitPrice() <= price) && (price <= info.getUpperLimitPrice());
    }

    public boolean isLimitPrice(String isinCode, LongShort longShort, int price) {
        LimitPriceInfo info = getLimitPriceInfo(isinCode);

        if (info == nullLimitPriceInfo || TempConf.IGNORE_LIMIT_PRICE)
            return false;

        if (longShort == LongShort.LONG && price == info.getUpperLimitPrice())
            return true;
        else return longShort == LongShort.SHORT && price == info.getLowerLimitPrice();
    }

    public int applyLimitPrice(String isinCode, LongShort longShort, int price) {
        LimitPriceInfo info = getLimitPriceInfo(isinCode);

        // 상하한가 정보가 없는 경우에는 기존 가격
        if (info == nullLimitPriceInfo || TempConf.IGNORE_LIMIT_PRICE)
            return price;

        // 상한가 매수, 하한가 매도일 때만 가격을 제한한다
        if (longShort == LongShort.LONG && price > info.getUpperLimitPrice())
            return info.getUpperLimitPrice();
        else if (longShort == LongShort.SHORT && price < info.getLowerLimitPrice())
            return info.getLowerLimitPrice();

        return price;
    }
}