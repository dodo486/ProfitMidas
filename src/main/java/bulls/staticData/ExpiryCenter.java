package bulls.staticData;

import bulls.dateTime.DateCenter;
import bulls.dateTime.TimeCenter;
import bulls.designTemplate.EarlyInitialize;
import bulls.hephaestus.collection.LpCalendar;
import bulls.log.DefaultLogger;
import bulls.staticData.ELW.ELWExtraInfo;
import bulls.staticData.ELW.ELWExtraInfoCenter;
import bulls.staticData.ELW.ELWInfo;
import bulls.staticData.ELW.ELWInfoCenter;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ExpiryCenter implements EarlyInitialize {
    Instance;

    private final ConcurrentHashMap<String, Integer> dayTillMaturityMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, LocalDateTime> codeExpiryMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> codeExpiryIdMap = new ConcurrentHashMap<>();

    // 만기일 -> 고유번호
    private final Map<LocalDateTime, Long> expiryIdMap = new HashMap<>();

    // 고유번호 -> 만기일
    private final Map<Long, LocalDateTime> idExpiryMap = new HashMap<>();

    // 만기일 -> 월물 표시 문자열
    private final Map<LocalDateTime, String> expiryStringMap = new HashMap<>();
    private final Map<LocalDateTime, String> weeklyExpiryStringMap = new HashMap<>();

    ExpiryCenter() {
        EquityInfoCenter.Instance.touch();
        FuturesInfoCenter.Instance.touch();
        for (var info : FuturesInfoCenter.Instance.getAllFuturesInfo()) {
            // getExpiry에서 만기를 구하면서 Map에 업데이트한다.
            getExpiry(info.isinCode);
        }
    }

    /**
     * <h2>만기일을 만기일 고유번호로 변환한다</h2>
     * <p>e.g. 2022-07-15T15:20 -> 202207151520</p>
     * <p>주의 : 이러한 변환은 사용자가 보기 편하게 하기 위해 바꾸는 것이므로
     * 만기일이 필요한 경우에는 만기일 고유번호를 파싱해서 만기일을 구하지 말고
     * getExpiryById() 함수를 사용해서 만기일을 가져와야 한다</p>
     *
     * @param expiry 만기일
     * @return 만기일 고유번호
     */
    private long convertExpiryId(LocalDateTime expiry) {
        int year = expiry.getYear();
        int month = expiry.getMonthValue();
        int day = expiry.getDayOfMonth();
        int hour = expiry.getHour();
        int minute = expiry.getMinute();

        long id = 0L;
        id += year;
        id *= 100;
        id += month;
        id *= 100;
        id += day;
        id *= 100;
        id += hour;
        id *= 100;
        id += minute;

        return id;
    }

    /**
     * <h2>만기일 (시간 포함)로 만기일 고유번호를 구한다</h2>
     *
     * @param expiry 만기일 (시간 포함)
     * @return 만기일 고유번호
     */
    public long getExpiryId(@NotNull LocalDateTime expiry) {
        if (expiryIdMap.containsKey(expiry))
            return expiryIdMap.get(expiry);

        long id = convertExpiryId(expiry);
        expiryIdMap.put(expiry, id);
        idExpiryMap.put(id, expiry);
        return id;
    }

    /**
     * <h2>만기일 (시간 제외)로 만기일 고유번호를 구한다</h2>
     *
     * <p>시간은 TimeCenter의 파생상품 최종 거래 시간으로 설정된다.</p>
     *
     * @param expiryDate 만기일 (시간 제외)
     * @return 만기일 고유번호
     */
    public long getExpiryId(@NotNull LocalDate expiryDate) {
        return getExpiryId(LocalDateTime.of(expiryDate, TimeCenter.Instance.DEFAULT_DERIVATIVES_EXPIRY_TIME));
    }

    /**
     * <h2>의무/만기 캘린더로 로 만기일 고유번호를 구한다</h2>
     *
     * <p>시간은 TimeCenter의 파생상품 최종 거래 시간으로 설정된다.</p>
     *
     * @param cal 의무/만기 캘린더
     * @return 만기일 고유번호
     */
    public long getExpiryId(@NotNull LpCalendar cal) {
        return getExpiryId(cal.getExpiryDate());
    }

    /**
     * <h2>해당 종목의 만기일 고유번호를 구한다</h2>
     *
     * @param isinCode 종목코드
     * @return 만기일 고유번호
     */
    public long getExpiryId(@NotNull String isinCode) {
        if (codeExpiryIdMap.containsKey(isinCode))
            return codeExpiryIdMap.get(isinCode);

        LocalDateTime expiry = getExpiry(isinCode);
        if (expiry == null)
            return -1;

        return getExpiryId(expiry);
    }

    /**
     * <h2>해당 종목의 만기일로 월물 표시 문자열을 반환한다</h2>
     * <p>e.g. 만기일 22/08/11, 위클리 아님 -> "2208"</p>
     * <p>e.g. 만기일 22/08/18 (8월 셋째주 목요일), 위클리 -> "08W3"</p>
     *
     * @param isinCode 종목 코드
     * @param isWeekly 위클리 옵션 여부
     * @return 월물 표시 문자열
     */
    public String getExpiryString(String isinCode, boolean isWeekly) {
        LocalDateTime expiry = getExpiry(isinCode);
        if (expiry == null)
            return null;

        return getExpiryString(expiry, isWeekly);
    }

    /**
     * <h2>해당 종목의 만기일로 월물 표시 문자열을 반환한다</h2>
     * <p>e.g. 만기일 22/08/11, 위클리 아님 -> "2208"</p>
     * <p>e.g. 만기일 22/08/18 (8월 셋째주 목요일), 위클리 -> "08W3"</p>
     *
     * @param expiry   만기일
     * @param isWeekly 위클리 옵션 여부
     * @return 월물 표시 문자열
     */
    public String getExpiryString(LocalDateTime expiry, boolean isWeekly) {
        if (isWeekly && weeklyExpiryStringMap.containsKey(expiry))
            return weeklyExpiryStringMap.get(expiry);
        if (!isWeekly && expiryStringMap.containsKey(expiry))
            return expiryStringMap.get(expiry);

        StringBuilder sb = new StringBuilder();
        int year = expiry.getYear() % 100;
        int month = expiry.getMonthValue();
        if (!isWeekly) {
            if (year < 10)
                sb.append("0");
            sb.append(year);
        }

        if (month < 10)
            sb.append("0");
        sb.append(month);

        if (isWeekly) {
            int weekOfMonth = 1 + (expiry.getDayOfMonth() - 1) / 7;
            sb.append("W").append(weekOfMonth);
        }

        String expiryString = sb.toString();
        if (isWeekly)
            weeklyExpiryStringMap.put(expiry, expiryString);
        else
            expiryStringMap.put(expiry, expiryString);

        return expiryString;
    }

    /**
     * <h2>만기일 고유번호로 월물 표시 문자열을 반환한다</h2>
     * <p>e.g. 만기일 22/08/11, 위클리 아님 -> "2208"</p>
     * <p>e.g. 만기일 22/08/18 (8월 셋째주 목요일), 위클리 -> "08W3"</p>
     *
     * @param expiryId 만기일 고유번호
     * @param isWeekly 위클리 옵션 여부
     * @return 월물 표시 문자열
     */
    public String getExpiryString(long expiryId, boolean isWeekly) {
        LocalDateTime expiry = getExpiryById(expiryId);
        if (expiry == null)
            return null;

        return getExpiryString(expiry, isWeekly);
    }

    /**
     * <h2>만기일 고유번호로 만기일을 구한다</h2>
     *
     * @param expiryId 만기일 고유번호
     * @return 만기일
     */
    public LocalDateTime getExpiryById(long expiryId) {
        return idExpiryMap.get(expiryId);
    }

    /**
     * <h2>해당 종목의 만기일까지 남은 일수를 반환</h2>
     * <p>파생상품이나 ELW는 해당 종목의 만기일까지 남은 일수를 반환하고 현물일 경우 0을 반환한다.</p>
     *
     * @param isinCode 종목코드
     * @return 해당 종목의 만기일까지 남은 일수
     */
    public int getDayTillMaturity(String isinCode) {
        if (dayTillMaturityMap.containsKey(isinCode))
            return dayTillMaturityMap.get(isinCode);

        ProdType pType = ProdTypeCenter.Instance.getProdType(isinCode);
        if (pType.isEquity() && !pType.isEquityELW()) {
            DefaultLogger.logger.error("{}는 현물이므로 만기까지 남은 일수를 0으로 설정합니다.", isinCode);
            dayTillMaturityMap.put(isinCode, 0);
            return 0;
        }

        LocalDate today = TimeCenter.Instance.today;
        var expiry = getExpiry(isinCode);

        // 만기일을 가져올 수 없는 경우
        if (expiry == null) {
            DefaultLogger.logger.error("{}는 만기 정보가 없어 만기까지 남은 일수를 0으로 설정합니다.", isinCode);
            dayTillMaturityMap.put(isinCode, 0);
            return 0;
        }

        int dayDiff;
        if (expiry.getYear() == today.getYear()) {
            dayDiff = expiry.getDayOfYear() - today.getDayOfYear();
        } else {
            dayDiff = 365 - today.getDayOfYear() + expiry.getDayOfYear();
        }

        dayTillMaturityMap.put(isinCode, dayDiff);
        return dayDiff;
    }

    /**
     * <h2>현재 시점부터 해당 종목의 만기까지 남은 년수를 반환</h2>
     * <p>현재 시점부터 만기까지 남은 시간을 년수로 환산한다. (365일 기준) 만기일 정보가 없는 경우 0을 반환한다.</p>
     *
     * @param isinCode 종목코드
     * @return 해당 종목의 만기까지 남은 년수
     */
    public double getTimeTillMaturity(String isinCode) {
        return getTimeTillMaturity(TimeCenter.Instance.getDateTimeAsLocalDateTimeType(), isinCode);
    }

    /**
     * <h2>지정한 시점부터 해당 종목의 만기까지 남은 년수를 반환</h2>
     * <p>지정한 시점부터 만기까지 남은 시간을 년수로 환산한다. (365일 기준) 만기일 정보가 없는 경우 0을 반환한다.</p>
     *
     * @param dateTime 지정한 시점
     * @param isinCode 종목코드
     * @return 지정한 시점부터 해당 종목의 만기까지 남은 년수
     */
    public double getTimeTillMaturity(LocalDateTime dateTime, String isinCode) {
        LocalDateTime expiry = getExpiry(isinCode);
        if (expiry == null) {
            DefaultLogger.logger.error("{}는 만기 정보가 없어 만기까지 남은 시간을 0으로 반환합니다.", isinCode);
            return 0;
        }

        long minutes = Duration.between(dateTime, expiry).toMinutes();
        return minutes / (60.0 * 24) / 365.0;
    }

    /**
     * <h2>지정한 시점부터 해당 종목의 만기까지 남은 년수를 반환</h2>
     * <p>지정한 시점부터 만기까지 남은 시간을 년수로 환산한다. (365일 기준) 만기일 정보가 없는 경우 0을 반환한다.</p>
     *
     * @param dateTime 지정한 시점
     * @param expiryId 만기일 고유번호
     * @return 지정한 시점부터 해당 종목의 만기까지 남은 년수
     */
    public double getTimeTillMaturity(LocalDateTime dateTime, long expiryId) {
        LocalDateTime expiry = getExpiryById(expiryId);
        if (expiry == null) {
            DefaultLogger.logger.error("expiryId : {}에 할당된 만기 정보가 없어 만기까지 남은 시간을 0으로 반환합니다.", expiryId);
            return 0;
        }

        long minutes = Duration.between(dateTime, expiry).toMinutes();
        return minutes / (60.0 * 24) / 365.0;
    }

    /**
     * <h2>해당 종목의 만기일 반환</h2>
     * <p>파생상품이나 ELW는 해당 종목의 만기일을 반환하고 현물이거나 정보가 없는 경우 null을 반환한다.</p>
     *
     * @param isinCode 종목코드
     * @return 해당 종목의 만기일
     */
    public LocalDateTime getExpiry(String isinCode) {
        if (codeExpiryMap.containsKey(isinCode))
            return codeExpiryMap.get(isinCode);

        ProdType pType = ProdTypeCenter.Instance.getProdType(isinCode);
        if (pType.isEquity() && !pType.isEquityELW()) {
            DefaultLogger.logger.error("{}는 현물이므로 만기가 없습니다.", isinCode);
            return null;
        }

        LocalDate expiryDate;
        if (pType.isEquityELW()) {
            ELWInfo info = ELWInfoCenter.Instance.getELWInfo(isinCode);
            if (info == null) {
                ELWExtraInfo extraInfo = ELWExtraInfoCenter.Instance.getELWExtraInfo(isinCode);
                if (extraInfo == null) {
                    DefaultLogger.logger.info(isinCode + "는 ELW인 것 같지만 ELW 정보가 없어 만기를 구할 수 없습니다.");
                    return null;
                }

                expiryDate = DateCenter.Instance.getLocalDateFromDateInt(extraInfo.최종거래일자);
            } else {
                expiryDate = DateCenter.Instance.getLocalDateFromDateInt(info.matDate);
            }
        } else {
            FuturesInfo info = FuturesInfoCenter.Instance.getFuturesInfo(isinCode);
            expiryDate = TimeCenter.getDateAsLocalDateType(info.만기);
        }

        var expiry = LocalDateTime.of(expiryDate, TimeCenter.Instance.DEFAULT_DERIVATIVES_EXPIRY_TIME);
        long expiryId = getExpiryId(expiryDate);
        codeExpiryMap.put(isinCode, expiry);
        codeExpiryIdMap.put(isinCode, expiryId);
        return expiry;
    }
}
