package bulls.hephaestus.collection;

import bulls.hephaestus.collection.enums.LpCalendarType;
import bulls.log.DefaultLogger;
import bulls.staticData.TempConf;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LpCalendar {

    final LpCalendarType calendarType;
    final DutyPeriod periodType;
    final LocalDate startDate;
    final LocalDate endDate;
    public final int year;
    public final int month;
    public final int nextYear;
    public final int nextMonth;
    public final char yearCode;
    public final char monthCode;
    public final char nextYearCode;
    public final char nextMonthCode;
    final LocalDateTime endDateMarketEnd;

    public LpCalendar(LpCalendarType type, String periodTypeStr, String target, LocalDate startDate, LocalDate endDate) {
        this.calendarType = type;
        this.startDate = startDate;
        this.endDate = endDate;
        int year, month;

        try {
            year = Integer.parseInt(target.substring(0, 4));
            month = Integer.parseInt(target.substring(4, 6));
        } catch (Exception e) {
            year = endDate.getYear();
            month = endDate.getMonthValue();
        }

        this.year = year;
        this.month = month;

        monthCode = getMonthChar(month);
        yearCode = getYearChar(year);


        switch (periodTypeStr) {
            case "Q" -> periodType = DutyPeriod.QUARTER;
            case "M" -> periodType = DutyPeriod.MONTH;
            case "M_Vkospi" -> periodType = DutyPeriod.MONTH_VKOSPI;
            default -> periodType = DutyPeriod.MONTH;
        }


        nextMonth = periodType.getNextExpiryMonth(month);
        nextYear = periodType.getNextExpiryYear(year, month);

        nextMonthCode = getMonthChar(nextMonth);
        nextYearCode = getYearChar(nextYear);
        endDateMarketEnd = endDate.atStartOfDay().plusHours(TempConf.MARKET_END_HOUR).plusMinutes(TempConf.MARKET_END_MINUTE);

        DefaultLogger.logger.info(toString());
    }

    public static char getMonthChar(int expireMonth) {
        if (expireMonth >= 10)
            return (char) (expireMonth + 55);

        return (char) (expireMonth + 48);
    }

    public static char getYearChar(int year) {
        int asciiAdder;
        if (year < 2019)
            asciiAdder = 66;
        else
            asciiAdder = 67;

        return (char) (year - 2006 + asciiAdder);
    }

    public enum DutyPeriod {
        QUARTER(3),
        MONTH(1),
        MONTH_VKOSPI(1);

        final int increment;

        DutyPeriod(int increment) {
            this.increment = increment;
        }

        public int getNextExpiryMonth(int month) {
            int nextMonth = month + increment;
            if (nextMonth > 12) {
                return increment;
            } else {
                return nextMonth;
            }
        }

        public int getNextExpiryYear(int year, int month) {
            if (month == 12)
                return year + 1;
            return year;
        }
    }


    @Override
    public String toString() {
        String msg = String.format("%s 정보 %s ~ %s  Expiry 코드:%c%c (%d %d) , 차근월물 Expiry 코드 :%c%c (%d %d)", getClass().getSimpleName(), startDate, endDate,
                yearCode, monthCode, year, month,
                nextYearCode, nextMonthCode, nextYear, nextMonth);
        return msg;
    }

    public boolean contains(LocalDate date) {
        if (date.isEqual(startDate) || date.isEqual(endDate))
            return true;
        return date.isAfter(startDate) && date.isBefore(endDate);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isRecentProduct(String isinCode) {
        return isinCode.charAt(6) == yearCode && isinCode.charAt(7) == monthCode;
    }

    public boolean isNextProduct(String isinCode) {
        return isinCode.charAt(6) == nextYearCode && isinCode.charAt(7) == nextMonthCode;
    }

    public boolean isEnd(LocalDate date) {
        return date.isEqual(endDate);
    }

    public int daysTillExpiry(LocalDate date) {
        return endDate.compareTo(date);
    }

    public int daysFromLastExpiry(LocalDate date) {
        return startDate.compareTo(date);
    }

    public boolean isStart(LocalDate date) {
        return date.isEqual(startDate);
    }

    public LpCalendarType getCalendarType() {
        return calendarType;
    }

    public DutyPeriod getPeriodType() {
        return periodType;
    }

    public LocalDate getExpiryDate() {
        if (calendarType == LpCalendarType.EXPIRY)
            return endDate;

        var expiryCal = switch (periodType) {
            case MONTH, MONTH_VKOSPI -> ExpiryMaster.Instance.getMonthlyCalendar(endDate);
            case QUARTER -> ExpiryMaster.Instance.getQuarterlyCalendar(endDate);
        };

        if (expiryCal == null) {
            DefaultLogger.logger.error(this + " 의무 캘린더에 대응하는 만기 캘린더를 찾을 수 없었습니다.");
            return null;
        }

        return expiryCal.getEndDate();
    }
}
