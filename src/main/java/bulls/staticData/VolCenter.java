package bulls.staticData;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import bulls.bs.CallPut;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.mongodb.MyMongoDB;
import bulls.hephaestus.HephaLogType;
import bulls.hephaestus.document.ServerMsgDoc;
import bulls.log.DefaultLogger;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import bulls.tcp.sync.TempClientSelector;
import bulls.tool.pricing.BS;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 종목, 행사가 별로 vol 을 저장
 */
public enum VolCenter {
    Instance;

    private static final double volFixLimit = 0.1;

    VolCenter() {
        volMap = new HashMap<>();
        updateAllVolFromDB();
    }

    private final HashMap<OptionIdentifier, Double> volMap;

    public Double getVol(OptionIdentifier id) {
        return volMap.get(id);
    }

    public List<Map.Entry<OptionIdentifier, Double>> getAllExistingVol() {
        return volMap.entrySet().stream()
                .filter(en -> en.getValue() != 0)
                .collect(Collectors.toList());
    }

    public void updateVol(OptionIdentifier id, double optionPrice, double underlyingPrice) {
        double t = id.getTimeTillExpiry();
        double vol = BS.bs_impliedVol(id.getCallPut(), underlyingPrice, id.getStrikePrice(), t, TempConf.조달금리, 0, optionPrice);
        updateVol(id, vol);
    }

    public Double updateVol(OptionIdentifier id, double vol) {
        if (vol <= 0 || !Double.isFinite(vol)) {
//            DefaultLogger.logger.info(" {} 종목 vol 값은 음수 {} 또는 NaN/Infinite으로 셋팅 될 수 없습니다.", AliasManager.Instance.getKoreanFromIsin(id.getIsinCode()), vol);
            return null;
        }

        Double previousVol = volMap.get(id);
        if (previousVol == null || previousVol == 0 || previousVol.isNaN() || previousVol.isInfinite()) {
            TempClientSelector.sendLogMsg("vol설정", AliasManager.Instance.getKoreanFromIsin(id.getIsinCode()) + "종목의 vol 이 option_vol 테이블에 없었지만 새 값을 설정했습니다." + vol);
            volMap.put(id, vol);
            updateDB(id, vol);
            return vol;
        }

        if (previousVol > 3) {
            volMap.put(id, vol);
            updateDB(id, vol);
            return vol;
        }

        double diff = Math.abs(previousVol - vol);
        if (diff < volFixLimit) {
            volMap.put(id, vol);
            updateDB(id, vol);
            return vol;
        }

        double limitedVol;
        if (vol > previousVol)
            limitedVol = previousVol + volFixLimit;
        else
            limitedVol = Math.max(0, previousVol - volFixLimit);


        volMap.put(id, limitedVol);
        updateDB(id, limitedVol);
        return limitedVol;
    }

    public void updateDB(OptionIdentifier id, double vol) {
        MongoCollection col;
        if (OptionIdentifierCenter.Instance.isSO(id.getIsinCode())) {
            col = MyMongoDB.Instance.getMongoCollection(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.OPTION_VOL);
        } else {
            col = MyMongoDB.Instance.getMongoCollection(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.INDEX_OPTION_VOL);
        }
        Bson filter;
        if (id.getCallPut() == CallPut.CALL)
            filter = new Document("callIsinCode", id.getIsinCode());
        else
            filter = new Document("putIsinCode", id.getIsinCode());
//                            .append("underlyingIsinCode",id.getUnderlyingCode())
//                            .append("strikePrice", id.getStrikePrice())
//                            .append("matDate", TimeCenter.getLocalDateAsDateType(id.get만기()));
        Bson newValue;
        if (id.getCallPut() == CallPut.CALL)
            newValue = new Document("callVol", vol);
        else
            newValue = new Document("putVol", vol);

        Bson updateOperationDoc = new Document()
                .append("$set", newValue);
        UpdateResult ur = col.updateOne(filter, updateOperationDoc, (new UpdateOptions()).upsert(true));
    }

    public void updateAllVolFromDB() {
        MongoCollection col = MyMongoDB.Instance.getMongoCollection(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.OPTION_VOL);

        MongoCursor<Document> result = col.find().iterator();
        loadVol(result);
        DefaultLogger.logger.debug("SO Vol update Completed!!!");

        MongoCollection colIndex = MyMongoDB.Instance.getMongoCollection(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.INDEX_OPTION_VOL);

        MongoCursor<Document> rIndex = colIndex.find().iterator();
        loadVol(rIndex);
        DefaultLogger.logger.debug("Index Vol update Completed!!!");

    }

    public void updateVolFromDB(String underLying) {
        MongoCollection<Document> col = MyMongoDB.Instance.getMongoCollection(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.OPTION_VOL);
        BsonDocument queryStockOption = new BsonDocument();
        String isin;

        ProdType pType = ProdTypeCenter.Instance.getProdType(underLying);
        if (pType.isStockFut()) {
            FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(underLying);
            isin = info.underlyingIsinCode;
        } else
            isin = underLying;

        if (isin == null)
            queryStockOption.append("underlyingIsinCode", new BsonString(isin));

        MongoCursor<Document> result = col.find(queryStockOption).iterator();
        loadVol(result);
    }

    public void updateIndexVolFromDB(DerivativesUnderlyingType underlyingType) {
        MongoCollection<Document> col = MyMongoDB.Instance.getMongoCollection(MongoDBDBName.PRICING_DATA, MongoDBCollectionName.INDEX_OPTION_VOL);
        BsonDocument queryIndexOption = new BsonDocument();
        queryIndexOption.append("underlyingIsinCode", new BsonString(underlyingType.toString()));
        MongoCursor<Document> result = col.find(queryIndexOption).iterator();
        loadVol(result);
    }

    private void loadVol(MongoCursor<Document> result) {
        while (result.hasNext()) {
            Document dOb = result.next();

            try {
                Double callVol = ((Number) dOb.get("callVol")).doubleValue();
                Double putVol = ((Number) dOb.get("putVol")).doubleValue();
                String callCode = (String) dOb.get("callIsinCode");
                String putCode = (String) dOb.get("putIsinCode");
                Date matDate = dOb.getDate("matDate");

                LocalDate maturity = TimeCenter.getDateAsLocalDateType(matDate);
                // 만기 지난 상품은 올리지 않음
                if (maturity.isBefore(TimeCenter.Instance.today))
                    continue;


                OptionIdentifier idCall = OptionIdentifierCenter.Instance.getOptionId(callCode);
                volMap.put(idCall, callVol);
                OptionIdentifier idPut = OptionIdentifierCenter.Instance.getOptionId(putCode);
                volMap.put(idPut, putVol);
            } catch (ClassCastException e) {
                DefaultLogger.logger.error(dOb.toString(), e);
            } catch (NullPointerException e) {
                DefaultLogger.logger.error(" Null found from DB : {}", dOb);
                String sb = MongoDBDBName.BATCH +
                        "/" +
                        MongoDBCollectionName.OPTION_VOL +
                        " 의 데이터가 기대와 다름:" +
                        dOb;

                ServerMsgDoc msg = ServerMsgDoc.now(HephaLogType.운영장애, "DBNullFound", sb);
                msg.fire();
            }
        }
    }
}
