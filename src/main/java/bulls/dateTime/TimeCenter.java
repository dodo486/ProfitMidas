package bulls.dateTime;

import bulls.hephaestus.collection.DayOff;
import bulls.log.DefaultLogger;
import bulls.staticData.TempConf;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public enum TimeCenter {
    Instance;
    long currentTime = 0;
    boolean isLiveMode = true;
    public LocalDateTime todayMidnight;
    public LocalDate today;
    public LocalDate lastWorkingDay;
    public LocalDateTime nextMarketStart;
    public LocalDateTime todayMarketStart;
    public LocalDateTime todayMarketEnd;
    public LocalDateTime todayDerivMarketEnd;
    public long marketStartTimeMill;
    public long marketEndTimeMill;
    public long marketHourInTimeMill;

    public long marketMinutes;

    public Date todayDate;
    public Date lastWorkingDayDate;

    public long initMilliTime;
    public long initNanoTime;

    long nextSecondTickTimeInMS = 0;
    private static final DateTimeFormatter formatYYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter formatYYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 파생상품의 최종 거래 시간은 지수/주식 선물/옵션의 최종 거래 시간인 15:20으로 정한다
    // Todo : 15:20에 종료하지 않는 선물을 매매하는 경우 해당 선물을 고려하도록 코드 변경해야 함
    public final LocalTime DEFAULT_DERIVATIVES_EXPIRY_TIME = LocalTime.of(15, 20, 0);

    public String getTodayStrYYYYMMDD() {
        return getDateAsLocalDateType().format(formatYYYYMMDD);
    }

    public String getTodayStrYYYY_MM_DD() {
        return getDateAsLocalDateType().format(formatYYYY_MM_DD);
    }

    final ConcurrentHashMap<String, Consumer<LocalDateTime>> simulationNextSecondTickEventListenerMap = new ConcurrentHashMap<>();

    TimeCenter() {
        today = LocalDate.now(ZoneId.systemDefault());
        init(today);
    }

    private void init(LocalDate today) {
        LocalTime midnight = LocalTime.MIDNIGHT;
        this.today = today;
        todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());

        todayMarketStart = today.atStartOfDay().plusHours(TempConf.MARKET_START_HOUR).plusMinutes(TempConf.MARKET_START_MINUTE);
        todayMarketEnd = today.atStartOfDay().plusHours(TempConf.MARKET_END_HOUR).plusMinutes(TempConf.MARKET_END_MINUTE);
        todayDerivMarketEnd = today.atStartOfDay().plusHours(TempConf.MARKET_END_HOUR).plusMinutes(TempConf.MARKET_END_MINUTE + 25);
        marketMinutes = ChronoUnit.MINUTES.between(todayMarketStart, todayMarketEnd);
        marketStartTimeMill = getLocalDateTimeAsDateType(todayMarketStart).getTime();
        marketEndTimeMill = getLocalDateTimeAsDateType(todayMarketEnd).getTime();
        marketHourInTimeMill = marketEndTimeMill - marketStartTimeMill;
        todayMidnight = LocalDateTime.of(today, midnight);
        LocalDate usualLastWorkingDay = TimeCenter.getUsualLastWorkingDayOf(today);

        int calendarDaysTillNextMarketStart = DayOff.Instance.getCalendarDaysTillNextMarketStart(today);
        nextMarketStart = todayMarketStart.plusDays(calendarDaysTillNextMarketStart);
        // 평상시엔 1일을 market-hour(6시간20분) 에 projection, 휴일이 껴 있으면 그만큼 비율이 커짐
        // 휴일이 여러일 겹쳐 있을 경우 하루는 빼고 계산: 연휴 직전 종가와 연휴 직후 까지의 시간을 1일로 처리
        if (calendarDaysTillNextMarketStart > 1)
            calendarDaysTillNextMarketStart = calendarDaysTillNextMarketStart - 1;

        //휴일이면 아닐때까지
        while (DayOff.Instance.isDayOff(usualLastWorkingDay)) {
            usualLastWorkingDay = TimeCenter.getUsualLastWorkingDayOf(usualLastWorkingDay);
        }
        lastWorkingDay = usualLastWorkingDay;
        DefaultLogger.logger.info("마지막 Working day : {} 로 초기화합니다.", lastWorkingDay);

        lastWorkingDayDate = Date.from(lastWorkingDay.atStartOfDay(ZoneId.systemDefault()).toInstant());

        initMilliTime = System.currentTimeMillis();
        initNanoTime = System.nanoTime();
    }

    /**
     * 시뮬레이션 시세 변화시 시간 변화 이벤트를 받을 리스너 설정
     *
     * @param key
     * @param listener
     */
    public void addSimulationNextSecondTickEventListener(String key, Consumer<LocalDateTime> listener) {
        simulationNextSecondTickEventListenerMap.put(key, listener);
    }

    void fireSimulationNextSecondTickEvent(LocalDateTime t) {
        if (t.getSecond() == 0) DefaultLogger.logger.info("TickEvent : {}", t);
        simulationNextSecondTickEventListenerMap.values().parallelStream().forEach(v -> {
            v.accept(t);
        });
    }

    public void setMode(boolean isLive) {
        isLiveMode = isLive;
    }

    public boolean isLiveMode() {
        return isLiveMode;
    }

    public void setDateAndTime(long t) {
        LocalDate tmpToday = Instant.ofEpochMilli(t).atZone(ZoneId.systemDefault()).toLocalDate();
        if (!today.equals(tmpToday)) {
            init(tmpToday);
        }
        currentTime = t;
        nextSecondTickTimeInMS = (t / 1000 + 1) * 1000;
    }

    public LocalDateTime getLocalDateTimeFromFeedTimestamp(long t) {
        //if(isLiveMode)
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(initMilliTime), ZoneId.systemDefault()).plusNanos(t - initNanoTime);
        //else
        //return LocalDateTime.ofInstant(Instant.ofEpochMilli(t), ZoneId.systemDefault());
    }

    public Date getDateFromFeedTimestamp(long t) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(initMilliTime), ZoneId.systemDefault()).plusNanos(t - initNanoTime);
        Date out = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        return out;
    }

    LocalDateTime nextSecondDt = LocalDateTime.MIN;

    public void setCurrentTimeForSimul(long t) {
        if (currentTime == 0) {
            initMilliTime = t;
            initNanoTime = System.nanoTime();
        }
        if (currentTime == t)
            return;
        currentTime = t;
        while (nextSecondTickTimeInMS <= t) {
            fireSimulationNextSecondTickEvent(LocalDateTime.ofInstant(Instant.ofEpochMilli(nextSecondTickTimeInMS), ZoneId.systemDefault()));
            nextSecondTickTimeInMS += 1000;
        }
    }

    public long getNanoTimeForSimul(long afterMilliTime) {
        return initNanoTime + (afterMilliTime - initMilliTime) * 1000000;
    }

    public long getMilliTimeBySimulNanoTime(long simulNanoTime) {
        return initMilliTime + (simulNanoTime - initNanoTime) / 1000000;
    }

    public void setDateAndTime(LocalDate localdate) {
        init(localdate);
    }


    public static LocalDate getUsualLastWorkingDayOf(LocalDate today) {
        LocalDate lastWorkingDay;
        if (today.getDayOfWeek() == DayOfWeek.MONDAY)
            lastWorkingDay = today.minusDays(3);
        else
            lastWorkingDay = today.minusDays(1);

        return lastWorkingDay;
    }

    public long getTime() {
        if (isLiveMode)
            return System.currentTimeMillis();
        else {
            if (currentTime == 0)
                DefaultLogger.logger.error("Invalid current time. Check whether time is set in Simulation mode.");
            return currentTime;
        }
    }

    public long getNanoTime() {
        if (isLiveMode)
            return System.nanoTime();
        else {
            if (currentTime == 0)
                DefaultLogger.logger.error("Invalid current time. Check whether time is set in Simulation mode.");
            return currentTime;
        }
    }

    public LocalDateTime getDateTimeAsLocalDateTimeType() {
        LocalDateTime currTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(getTime()), ZoneId.systemDefault());
        return currTime;
    }

    public LocalDate getDateAsLocalDateType() {
        LocalDateTime currTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(getTime()), ZoneId.systemDefault());
        return currTime.toLocalDate();
    }

    public LocalDateTime getDateAsLocalDateTimeType() {
        LocalDateTime currTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(getTime()), ZoneId.systemDefault());
        return currTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public Date getDateTimeAsDateType() {
        Date out = Date.from(getDateTimeAsLocalDateTimeType().atZone(ZoneId.systemDefault()).toInstant());
        return out;
    }

    public Date getDateAsDateType() {
        LocalDateTime currTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(getTime()), ZoneId.systemDefault());
        LocalDate currDate = currTime.toLocalDate();
        Date out = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return out;
    }


    public static LocalDateTime getDateTimeAsLocalDateTimeType(Date date) {
        LocalDateTime currTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        return currTime;
    }

    public static LocalDate getDateAsLocalDateType(Date date) {
        LocalDate ld = getDateTimeAsLocalDateTimeType(date).toLocalDate();
        return ld;
    }

    public static Date getLocalDateAsDateType(LocalDate currDate) {
        Date out = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return out;
    }

    public static Date getLocalDateTimeAsDateType(LocalDateTime currDateTime) {
        Date out = Date.from(currDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return out;
    }

    public static LocalDateTime getEpochAsLocalDateTimeType(long epoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
    }

    public static long getLocalDateTimeAsEpochType(LocalDateTime t) {
        return t.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static void main(String[] args) {

        int count = DayOff.Instance.getWorkingDayCountToPast(TimeCenter.Instance.today, LocalDate.now().minusDays(50));
        System.out.println(count);
        DefaultLogger.logger.debug("마지막 workingDay:{}", TimeCenter.Instance.lastWorkingDay.toString());

        System.out.println(TimeCenter.Instance.isStockMarketHour());
    }

    public double getMarketMinutePercentile(LocalDateTime ldt) {
        long elapsedMin = ChronoUnit.MINUTES.between(ldt, todayMarketStart);
        double percentile = elapsedMin * 1.0 / marketMinutes;
        return percentile;
    }

    public boolean isStockMarketHour() {
        if (isLiveMode) {
            LocalDateTime ldtNow = LocalDateTime.now();
            return !ldtNow.isBefore(todayMarketStart) && !ldtNow.isAfter(todayMarketEnd);
        } else {
            return currentTime >= marketStartTimeMill && currentTime <= marketEndTimeMill;
        }
    }

    /**
     * 기준일 이전 날짜 중 기준일과 가장 가까운 영업일 반환
     *
     * @param date 기준일
     * @return 기준일과 가장 가까운 영업일
     */
    public static LocalDate getLastWorkingDayOf(LocalDate date) {
        do {
            date = TimeCenter.getUsualLastWorkingDayOf(date);
        } while (DayOff.Instance.isDayOff(date));

        return date;
    }
}
