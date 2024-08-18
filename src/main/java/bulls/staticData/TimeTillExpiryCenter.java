package bulls.staticData;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import bulls.bs.CallPut;
import bulls.dateTime.TimeCenter;
import bulls.log.DefaultLogger;
import bulls.staticData.ProdType.DerivativesUnderlyingType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public enum TimeTillExpiryCenter {
    Instance;

    private final long timeUnitInMilSec;
    // < Floor(currentTimeMil), expiryId (Long), t of BS>
    private final Table<Long, Long, Double> timeTillExpiry;

    TimeTillExpiryCenter() {
        timeTillExpiry = HashBasedTable.create();
        timeUnitInMilSec = TimeUnit.MINUTES.toMillis(1);
        initTable(TimeCenter.Instance.today);
    }

    /**
     * <h2>현재 시간을 기준으로 해당 종목의 시간 가치를 반환한다</h2>
     * <p>해당 종목의 만기 기준 테이블이 없다면 시간 가치 테이블을 생성한다.</p>
     * <p>주의 : 현재 시간이 아니라 환산 시간 기준으로 시간 가치를 계산한다.</p>
     *
     * @param isinCode 종목코드
     * @return 시간 가치 (만기까지 남은 년수)
     */
    public Double getTimeTillExpiry(String isinCode) {
        long expiryId = ExpiryCenter.Instance.getExpiryId(isinCode);
        if (expiryId < 0)
            return null;

        return getTimeTillExpiry(expiryId);
    }

    /**
     * <h2>현재 시간을 기준으로 해당 종목의 시간 가치를 반환한다</h2>
     * <p>해당 종목의 만기 기준 테이블이 없다면 시간 가치 테이블을 생성한다.</p>
     * <p>주의 : 현재 시간이 아니라 환산 시간 기준으로 시간 가치를 계산한다.</p>
     *
     * @param expiryId 만기일 고유번호
     * @return 시간 가치 (만기까지 남은 년수)
     */
    public Double getTimeTillExpiry(long expiryId) {
        long now = floor(TimeCenter.Instance.getTime());
        Double t = timeTillExpiry.get(now, expiryId);

        if (t == null) {
            addTable(expiryId);
            t = timeTillExpiry.get(now, expiryId);
        }

        return t;
    }

    /**
     * <h2>주어진 에포크 기준으로 해당 종목의 시간 가치를 반환한다</h2>
     * <p>해당 종목의 만기 기준 테이블이 없다면 시간 가치 테이블을 생성한다.</p>
     * <p>주의 : 현재 시간이 아니라 환산 시간 기준으로 시간 가치를 계산한다.</p>
     *
     * @param now      시간 가치를 계산할 에포크
     * @param expiryId 만기일 고유번호
     * @return 시간 가치 (만기까지 남은 년수)
     */
    public Double getTimeTillExpiry(long now, long expiryId) {
        Double t = timeTillExpiry.get(now, expiryId);

        if (t == null) {
            addTable(expiryId);
            t = timeTillExpiry.get(now, expiryId);
        }

        return t;
    }

    /**
     * <h2>주어진 에포크 기준으로 해당 종목의 시간 가치를 바로 계산해서 반환한다</h2>
     * <p>테이블이 이미 존재하더라도 다시 계산해서 반환한다.</p>
     * <p>주의 : 현재 시간이 아니라 환산 시간 기준으로 시간 가치를 계산한다.</p>
     *
     * @param now      시간 가치를 계산할 에포크
     * @param expiryId 만기일 고유번호
     * @return 시간 가치 (만기까지 남은 년수)
     */
    public Double getTimeTillExpiryWithoutTable(long now, int expiryId) {
        var expiry = ExpiryCenter.Instance.getExpiryById(expiryId);
        if (expiry == null)
            return null;

        long projection;
        if (expiry.toLocalDate().equals(TimeCenter.Instance.today))
            projection = TimeProjectionCenter.Instance.getProjectionTimeAtMaturity(now);
        else
            projection = TimeProjectionCenter.Instance.getProjectionTimeAtNextWorkingDay(now);

        LocalDateTime projectionTime = TimeCenter.getEpochAsLocalDateTimeType(projection);
        return ExpiryCenter.Instance.getTimeTillMaturity(projectionTime, expiryId);
    }

    private long floor(long currentTimeMil) {
        return currentTimeMil - (currentTimeMil % timeUnitInMilSec);
    }

    private void addTable(long expiryId) {
        long now = floor(TimeCenter.Instance.getTime());
        long end = now + 10 * 60 * 60 * 1000;
        LocalDateTime closeTime = ExpiryCenter.Instance.getExpiryById(expiryId);
        if (closeTime == null) {
            DefaultLogger.logger.error("expiryId : {} 에 대한 TimeTillMaturity 초기화 실패!", expiryId);
            return;
        }

        // 오늘이 만기인 경우
        if (closeTime.toLocalDate().equals(TimeCenter.Instance.today)) {
            while (now < end) {
//                LocalDateTime nowDateTime = TimeCenter.getEpochAsLocalDateTimeType(now);
//                LocalDateTime projectionTime = TimeProjectionCenter.Instance.getProjectionTimeAtMaturity(nowDateTime);
                long projection = TimeProjectionCenter.Instance.getProjectionTimeAtMaturity(now);
                LocalDateTime projectionTime = TimeCenter.getEpochAsLocalDateTimeType(projection);
                Double tTillExpiry = ExpiryCenter.Instance.getTimeTillMaturity(projectionTime, expiryId);
                timeTillExpiry.put(now, expiryId, tTillExpiry);
                now += timeUnitInMilSec;
            }
        } else {
            while (now < end) {
//                LocalDateTime nowDateTime = TimeCenter.getEpochAsLocalDateTimeType(now);
//                LocalDateTime projectionTime = TimeProjectionCenter.Instance.getProjectionTimeAtNextWorkingDay(nowDateTime);
                long projection = TimeProjectionCenter.Instance.getProjectionTimeAtNextWorkingDay(now);
                LocalDateTime projectionTime = TimeCenter.getEpochAsLocalDateTimeType(projection);
                Double tTillExpiry = ExpiryCenter.Instance.getTimeTillMaturity(projectionTime, expiryId);
                timeTillExpiry.put(now, expiryId, tTillExpiry);
                now += timeUnitInMilSec;
            }
        }
    }

    private void initTable(LocalDate today) {
        // 구동시점으로부터 10시간에 대해서 미리 계산
        long now = floor(TimeCenter.Instance.getTime());
        long end = now + 10 * 60 * 60 * 1000;

        // 미리 시간가치를 계산하는 기초자산 : 3개월물(K2I), 1개월물(MKI), 변동성 선물(WKI), 위클리 옵션(VKI)
        DerivativesUnderlyingType[] duts = {DerivativesUnderlyingType.K2I, DerivativesUnderlyingType.MKI, DerivativesUnderlyingType.WKI, DerivativesUnderlyingType.VKI};
        int[] maturityNumbers = {0, 1};

        Set<Long> expiryIdSet = new HashSet<>();
        for (var dut : duts) {
            for (int maturityNumber : maturityNumbers) {
                FuturesInfo info = FuturesInfoCenter.Instance.getFutures(dut, maturityNumber);
                if (info != null) {
                    long expiryId = ExpiryCenter.Instance.getExpiryId(info.isinCode);
                    expiryIdSet.add(expiryId);
                    continue;
                }

                // 선물이 없는 경우 옵션 확인
                FuturesInfo optInfo = FuturesInfoCenter.Instance.getATMOption(dut, CallPut.CALL, maturityNumber);
                if (optInfo != null) {
                    long expiryId = ExpiryCenter.Instance.getExpiryId(optInfo.isinCode);
                    expiryIdSet.add(expiryId);
                }
            }
        }

        for (long expiryId : expiryIdSet) {
            DefaultLogger.logger.info("{} : 미리 시간가치를 계산합니다.", ExpiryCenter.Instance.getExpiryById(expiryId));
            addTable(expiryId);
        }
    }
}

