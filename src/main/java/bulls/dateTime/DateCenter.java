package bulls.dateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public enum DateCenter {

    Instance;
    private final String yyyyMMddYesterday;
    private final String yyyyMMddDashed;
    private final String yyyyMMdd;
    private final String yyyyMMddTomorrow;
    private final Date date;
    private final LocalDate localDate;
    private final DateTimeFormatter dateTimeFormat;
    final DateTimeFormatter f;
    final DateTimeFormatter f2;
    final SimpleDateFormat d2;
    final DateTimeFormatter formatter_HHmmssSSS;


    DateCenter() {
        localDate = LocalDate.now();
        f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        yyyyMMddDashed = localDate.format(f);
        f2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        d2 = new SimpleDateFormat("yyyyMMdd");
        yyyyMMdd = localDate.format(f2);
        yyyyMMddYesterday = localDate.minusDays(1L).format(f2);
        yyyyMMddTomorrow = localDate.plusDays(1L).format(f2);

        Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        date = Date.from(instant);

        dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        formatter_HHmmssSSS = DateTimeFormatter.ofPattern("HHmmssSSS");
    }

    public String getTomorrowStr() {
        return yyyyMMddTomorrow;
    }

    public String getTodayStr() {
        return yyyyMMdd;
    }

    public String getYesterDayStr() {
        return yyyyMMddYesterday;
    }

    public String getTodayStrWithDash() {
        return yyyyMMddDashed;
    }

    public String getDateTime() {
        LocalDateTime dt = LocalDateTime.now();
        return dt.format(dateTimeFormat);
    }

    public Date getTodayDate() {
        return date;
    }

    public synchronized Date parse_yyyyMMdd(String yyyyMMdd) throws ParseException {
        return d2.parse(yyyyMMdd);
    }

    public LocalDate parse_yyyyMMdd_toLocalDate(String yyyyMMdd) {
        return LocalDate.parse(yyyyMMdd, f2);
    }

    public DateTimeFormatter getHHmmssSSSFormatter() {
        return formatter_HHmmssSSS;
    }

    public LocalDate getLocalDateFromDateInt(int yyyyMMdd) {
        int year, month, day;
        year = yyyyMMdd / 10000;
        yyyyMMdd %= 10000;
        month = yyyyMMdd / 100;
        day = yyyyMMdd % 100;

        return LocalDate.of(year, month, day);
    }
}
