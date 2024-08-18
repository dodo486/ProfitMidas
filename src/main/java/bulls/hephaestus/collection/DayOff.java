package bulls.hephaestus.collection;

import bulls.dateTime.DateCenter;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import org.bson.Document;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;

public enum DayOff {
    Instance;

    final HashSet<LocalDate> dayOffSet = new HashSet<>();

    public boolean isDayOff(LocalDate date) {
        return dayOffSet.contains(date);
    }

    // 금요일, 공휴일 T 가속을 위함
    public int getCalendarDaysTillNextMarketStart(LocalDate today) {
        int days = 1;
        LocalDate tomorrow = today;
        while (true) {
            tomorrow = tomorrow.plusDays(1);
            DayOfWeek dow = tomorrow.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY || isDayOff(tomorrow)) {
                days++;
                continue;
            }
            break;
        }
        return days;
    }

    public int getWorkingDayCountToPast(LocalDate today, LocalDate pastDate) {
        if (pastDate.isAfter(today))
            return -1;
        int nDays = 0;
        while (!today.isEqual(pastDate)) {
            DayOfWeek dow = today.getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY || isDayOff(today)) {
                today = today.minusDays(1);
                continue;
            }
            nDays++;
            today = today.minusDays(1);
        }
        return nDays;
    }

    DayOff() {
        Document query = new Document("dateType", "휴일");
        var col = DBCenter.Instance.findIterable(MongoDBDBName.MANAGE_DATA, MongoDBCollectionName.CALENDAR, query);
        for (Document d : col) {
            Integer beginInt = d.getInteger("beginDate");
            Integer endInt = d.getInteger("endDate");

            if (beginInt == null || endInt == null)
                continue;

            try {
                Date dateBegin = DateCenter.Instance.parse_yyyyMMdd(beginInt.toString());
                LocalDate ldBegin = TimeCenter.getDateAsLocalDateType(dateBegin);
                Date dateEnd = DateCenter.Instance.parse_yyyyMMdd(endInt.toString());
                LocalDate ldEnd = TimeCenter.getDateAsLocalDateType(dateEnd);

                dayOffSet.add(ldBegin);
                while (ldBegin.isBefore(ldEnd)) {
                    dayOffSet.add(ldBegin);
                    ldBegin = ldBegin.plusDays(1);
                }
                dayOffSet.add(ldEnd);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
