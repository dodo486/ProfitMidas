package bulls.feed.dc.filter;

import bulls.data.PriceInfo;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.log.DefaultLogger;
import bulls.staticData.AliasManager;
import bulls.staticData.TempConf;
import org.bson.Document;

import java.util.concurrent.ConcurrentHashMap;

// 보드 ID 구분
// G1: 정규장(시가단일가, 시가단일가 연장, 장중단일가, 장중단일가 연장, 접속)
// G2: 장개시전시간외 종가
// G3: 장종료후 시간외 종가
// G4: 장종료수 시간외 단일가

// Equity 체결을 통해 들어오는 누적 체결은 보드ID 구분에 따라 각각 누적값이 들어옴
// 반면 Equity 호가를 통해 들어오는 bid/Ask 정보에도 누적체결이 있는데 이 값은 오늘의 총 누적체결을 의미함 (G1+G2+G3+G4)
// 우리 관심사는 장종료 전이므로 G2, G1 만 합산하면 Equity 호가를 통해 들어오는 bid/Ask 의 누적체결과 비교하여 체결과 호가 어느시세가 최신인지 판단이 가능
// G2 체결에 대해서 실제 발생시점에만 체결을 통해 제공 될 뿐 이후 값을 확인할 방법이 없어서 캐시+DB 기록이 필수
public enum G2AccVolumeEquityContractValidator implements EarlyInitialize, EquityContractValidator {
    Instance;

    private boolean updateDB = TempConf.UPDATE_G2_ACC_VOLUME_TO_DB;

    private final ConcurrentHashMap<String, Long> accG2TradingVolumeMap;
    private final ConcurrentHashMap<String, String> keyForLoggingMap;

    G2AccVolumeEquityContractValidator() {
        accG2TradingVolumeMap = new ConcurrentHashMap<>();
        keyForLoggingMap = new ConcurrentHashMap<>();
        initFromDB();
    }

    public void setUpdateDB() {
        updateDB = true;
    }

    public void initFromDB() {
        var col = DBCenter.Instance.findIterable(MongoDBDBName.MARKET, MongoDBCollectionName.G2_ACC_TRADING_VOLUME);
        for (Document d : col) {
            String isinCode = d.getString("isinCode");
            Long dbVolume = d.getLong("volume");
            // update only when DB volume is bigger than cached one.
            long cachedVolume = accG2TradingVolumeMap.getOrDefault(isinCode, 0L);
            if (dbVolume > cachedVolume) {
                accG2TradingVolumeMap.put(isinCode, dbVolume);
                DefaultLogger.logger.info("{} 종목의 G2 누적 체결량을 DB 로부터 {} 로 초기화 합니다.",
                        AliasManager.Instance.getKoreanFromIsin(isinCode), dbVolume);
            }
        }
    }

    public void updateG2(String isinCode, long accVolume) {
        // update only when new accVolume is bigger than cached one.
        long cachedVolume = accG2TradingVolumeMap.getOrDefault(isinCode, 0L);
        if (accVolume >= cachedVolume) {
            accG2TradingVolumeMap.put(isinCode, accVolume);
            // update DB only when updateDB is true.
            // FeedToDBServer might call setUpdateDB() when it starts
            if (updateDB) {
                Document filter = new Document("isinCode", isinCode);
                Document newValue = new Document("isinCode", isinCode).append("volume", accVolume);
                DBCenter.Instance.update(MongoDBDBName.MARKET, MongoDBCollectionName.G2_ACC_TRADING_VOLUME, filter, newValue);
            }
        } else {
            DefaultLogger.logger.info("{} 종목 G2 누적 체결 값 {}이 기존 저장값 {}보다 작습니다.",
                    AliasManager.Instance.getKoreanFromIsin(isinCode),
                    accVolume, cachedVolume);
        }
    }

    @Override
    public boolean isLateG1Contract(PriceInfo info, long accVolumeFromBidAsk) {
        long accG2Volume = accG2TradingVolumeMap.getOrDefault(info.isinCode, 0L);
        long totalVolumeFromContract = accG2Volume + info.totalAmount;
        //            String key = keyForLoggingMap.computeIfAbsent(info.isinCode, k -> k + "_G1");
        //            if (OnceAPeriodLogger.Instance.isPrintPossible(10, ChronoUnit.MINUTES, key)) {
        //                OnceAPeriodLogger.Instance.writeLog(key,
        //                        "{} 체결량 기준 역전 발생 by 체결 {} > G2:{} + G1:{} = {} 호가피드={}",
        //                        AliasManager.Instance.getKoreanFromIsin(info.isinCode), info.amount,
        //                        accG2Volume, info.totalAmount, totalVolumeFromContract, accVolumeFromBidAsk);
        //            }
        return accVolumeFromBidAsk > totalVolumeFromContract;
    }
}

