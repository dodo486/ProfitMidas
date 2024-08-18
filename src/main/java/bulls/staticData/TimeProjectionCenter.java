package bulls.staticData;


import bulls.dateTime.TimeCenter;
import bulls.designTemplate.EarlyInitialize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * <h2>TimeProjectionCenter</h2>
 *
 * <p>파생상품의 시간 가치 계산을 위해 장 운영 시간을 특정 시간대로 투영하여 환산시간을 계산해준다.</p>
 * <p></p>
 *
 * <h3>만기일인 경우</h3>
 *
 * <p>* 장 시작 전 : 시간가치 감소 없음 (하루치가 남아있음)</p>
 * <p>* 장 증 : 9:00 -> 오늘 9:00, 15:20 -> 오늘 15:20으로 환산하여 하루 시간가치를 감소시킨다.</p>
 * <p>* 장 끝난 후 : 시간가치 0</p>
 * <p></p>
 *
 * <h3>만기일이 아닌 경우</h3>
 *
 * <p>* 장 시작 전 : 시간가치 감소 없음 (하루치가 남아 있음)</p>
 * <p>*장 증 : 9:00 -> 오늘 9:00, 15:20 -> 다음 영업일 9:00로 환산하여 하루 시간가치를 감소시킨다.</p>
 * <p>* 장 끝난 후 : 다음 영업일 시작 전의 시간가치</p>
 * <p></p>
 *
 * <h3>연휴일 때 시간 가치 보정</h3>
 *
 * <p>연휴인 경우 전체 날짜를 그대로 감으면 시간가치가 너무 빠르게 감소할 수 있다. (시간가치가 빠르게 감소하면 옵션 매도되는 경향이 있음)
 * <p>따라서 실제 날짜보다 어느정도 덜 감아주어야 한다.</p>
 * <p>TempConf의 TIME_TILL_EXPIRY_CORRECTION_RATIO_ON_HOLIDAY로 보정치를 조정할 수 있다.</p>
 * <p>e.g. TIME_TILL_EXPIRY_CORRECTION_RATIO_ON_HOLIDAY = 0.25인 경우</p>
 * <p>* 다음 영업일이 내일인 경우 (휴일 없음) : 변동 없이 1일치를 감는다.</p>
 * <p>* 다음 영업일이 4일 뒤인 경우 (휴일 3일) : 4 * (1 - 0.25) = 3일치만 감는다.</p>
 * <p></p>
 */
public enum TimeProjectionCenter implements EarlyInitialize {
    Instance;

    // 장 운영시간
    private final LocalDateTime marketStartTime, marketEndTime;
    private final long marketStartTimeEpoch, marketEndTimeEpoch;

    // 만기일이 아닌 경우 변환할 시간대 (영업일 기준 이론값)
    private final LocalDateTime theoProjectionStartTime, theoProjectionEndTime;
    private final long theoProjectionStartTimeEpoch, theoProjectionEndTimeEpoch;

    // 만기일이 아닌 경우 변환할 시간대 (보정치를 적용하여 실제 사용하는 값)
    private final LocalDateTime actualProjectionStartTime, actualProjectionEndTime;
    private final long actualProjectionStartTimeEpoch, actualProjectionEndTimeEpoch;

    // 만기일인 경우 변환할 시간대
    private final LocalDateTime projectionStartTimeAtMaturity, projectionEndTimeAtMaturity;
    private final long projectionStartTimeAtMaturityEpoch, projectionEndTimeAtMaturityEpoch;

    // 시간 변환 비율
    private final double theoProjectionRatio, actualProjectionRatio, projectionRatioAtMaturity;

    TimeProjectionCenter() {
        LocalDate today = TimeCenter.Instance.today;
        marketStartTime = TimeCenter.Instance.todayMarketStart;
        marketEndTime = TimeCenter.Instance.todayMarketEnd;

        double correctionRatioOnHolidays = TempConf.TIME_TILL_EXPIRY_CORRECTION_RATIO_ON_HOLIDAY;

        theoProjectionStartTime = actualProjectionStartTime = projectionStartTimeAtMaturity = LocalDateTime.of(today, marketStartTime.toLocalTime());

        LocalDate nextWorkingDay = TimeCenter.Instance.nextMarketStart.toLocalDate();
        // 만기일에 시간 가치가 0이 되는 시간을 장 종료시간 (15:20)으로 한다. 그렇지 않은 선물(상품 선물 등)을 매매할 경우 변경 필요함
        projectionEndTimeAtMaturity = LocalDateTime.of(today, marketEndTime.toLocalTime());

        theoProjectionEndTime = LocalDateTime.of(nextWorkingDay, marketStartTime.toLocalTime());

        // 휴일이 없는 경우에는 변경없이 내일 날짜를 사용
        if (nextWorkingDay.equals(today.plusDays(1))) {
            actualProjectionEndTime = theoProjectionEndTime;
        } else {    // 중간에 휴일이 있다면 시간가치를 덜 감도록 projectionEndTime을 앞당겨준다.
            var minutes = ChronoUnit.MINUTES.between(theoProjectionStartTime, theoProjectionEndTime);
            minutes *= (1 - correctionRatioOnHolidays);
            actualProjectionEndTime = theoProjectionStartTime.plusMinutes(minutes);
        }

        marketStartTimeEpoch = TimeCenter.getLocalDateTimeAsEpochType(marketStartTime);
        marketEndTimeEpoch = TimeCenter.getLocalDateTimeAsEpochType(marketEndTime);
        theoProjectionStartTimeEpoch = TimeCenter.getLocalDateTimeAsEpochType(theoProjectionStartTime);
        theoProjectionEndTimeEpoch = TimeCenter.getLocalDateTimeAsEpochType(theoProjectionEndTime);
        actualProjectionStartTimeEpoch = TimeCenter.getLocalDateTimeAsEpochType(actualProjectionStartTime);
        actualProjectionEndTimeEpoch = TimeCenter.getLocalDateTimeAsEpochType(actualProjectionEndTime);
        projectionStartTimeAtMaturityEpoch = TimeCenter.getLocalDateTimeAsEpochType(projectionStartTimeAtMaturity);
        projectionEndTimeAtMaturityEpoch = TimeCenter.getLocalDateTimeAsEpochType(projectionEndTimeAtMaturity);

        long marketHourSec = ChronoUnit.SECONDS.between(TimeCenter.Instance.todayMarketStart, TimeCenter.Instance.todayMarketEnd);
        long theoProjectionTimeSec = ChronoUnit.SECONDS.between(theoProjectionStartTime, theoProjectionEndTime);
        long actualProjectionTimeSec = ChronoUnit.SECONDS.between(actualProjectionStartTime, actualProjectionEndTime);
        long projectionTimeAtMaturitySec = ChronoUnit.SECONDS.between(projectionStartTimeAtMaturity, projectionEndTimeAtMaturity);

        actualProjectionRatio = (double) actualProjectionTimeSec / marketHourSec;
        theoProjectionRatio = (double) theoProjectionTimeSec / marketHourSec;
        projectionRatioAtMaturity = (double) projectionTimeAtMaturitySec / marketHourSec;
    }

    /**
     * <h2>오늘이 만기일이라고 가정했을 때 현재 시간으로 변환 시간을 구한다.</h2>
     *
     * @param time 현재 시간
     * @return 변환 시간
     */
    public LocalDateTime getProjectionTimeAtMaturity(LocalDateTime time) {
        if (time.isBefore(marketStartTime))
            return projectionStartTimeAtMaturity;

        if (time.isAfter(marketEndTime))
            return projectionEndTimeAtMaturity;

        // 만기일인 경우 장 중일 때는 현재 시간을 그대로 사용하면 되므로 ratio 계산이 필요 없음
        // (일반적인 경우 projectionRatioAtMaturity = 1이므로)
        return time;
    }

    /**
     * <h2>오늘이 만기일이 아니라고 가정했을 때 현재 시간으로 변환 시간을 구한다.</h2>
     *
     * @param time 현재 시간
     * @return 변환 시간
     */
    public LocalDateTime getProjectionTimeAtNextWorkingDay(LocalDateTime time) {
        if (time.isBefore(marketStartTime))
            return actualProjectionStartTime;

        if (time.isAfter(marketEndTime))
            return actualProjectionEndTime;

        long sec = ChronoUnit.SECONDS.between(marketStartTime, time);
        sec *= actualProjectionRatio;
        return actualProjectionStartTime.plusSeconds(sec);
    }

    /**
     * <h2>오늘이 만기일이라고 가정했을 때 현재 에포크로 변환 에포크를 구한다.</h2>
     *
     * @param epoch 현재 에포크
     * @return 변환 에포크
     */
    public long getProjectionTimeAtMaturity(long epoch) {
        if (epoch < marketStartTimeEpoch)
            return projectionStartTimeAtMaturityEpoch;

        if (marketEndTimeEpoch < epoch)
            return projectionEndTimeAtMaturityEpoch;

        // 만기일인 경우 장 중일 때는 현재 시간을 그대로 사용하면 되므로 ratio 계산이 필요 없음
        // (일반적인 경우 projectionRatioAtMaturity = 1이므로)
        return epoch;
    }

    /**
     * <h2>오늘이 만기일이 아니라고 가정했을 때 현재 에포크로 변환 에포크를 구한다.</h2>
     *
     * @param epoch 현재 에포크
     * @return 변환 에포크
     */
    public long getProjectionTimeAtNextWorkingDay(long epoch) {
        if (epoch < marketStartTimeEpoch)
            return actualProjectionStartTimeEpoch;

        if (marketEndTimeEpoch < epoch)
            return actualProjectionEndTimeEpoch;

        long ms = epoch - marketStartTimeEpoch;
        ms *= actualProjectionRatio;
        return actualProjectionStartTimeEpoch + ms;
    }

    @Override
    public String toString() {
        return "TimeProjectionCenter{" +
                "marketStartTime=" + marketStartTime +
                ", marketEndTime=" + marketEndTime +
                ", marketStartTimeEpoch=" + marketStartTimeEpoch +
                ", marketEndTimeEpoch=" + marketEndTimeEpoch +
                ", theoProjectionStartTime=" + theoProjectionStartTime +
                ", theoProjectionEndTime=" + theoProjectionEndTime +
                ", theoProjectionStartTimeEpoch=" + theoProjectionStartTimeEpoch +
                ", theoProjectionEndTimeEpoch=" + theoProjectionEndTimeEpoch +
                ", actualProjectionStartTime=" + actualProjectionStartTime +
                ", actualProjectionEndTime=" + actualProjectionEndTime +
                ", actualProjectionStartTimeEpoch=" + actualProjectionStartTimeEpoch +
                ", actualProjectionEndTimeEpoch=" + actualProjectionEndTimeEpoch +
                ", projectionStartTimeAtMaturity=" + projectionStartTimeAtMaturity +
                ", projectionEndTimeAtMaturity=" + projectionEndTimeAtMaturity +
                ", projectionStartTimeAtMaturityEpoch=" + projectionStartTimeAtMaturityEpoch +
                ", projectionEndTimeAtMaturityEpoch=" + projectionEndTimeAtMaturityEpoch +
                ", theoProjectionRatio=" + theoProjectionRatio +
                ", actualProjectionRatio=" + actualProjectionRatio +
                ", projectionRatioAtMaturity=" + projectionRatioAtMaturity +
                '}';
    }
}
