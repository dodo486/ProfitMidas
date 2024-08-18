package bulls.hephaestus.collection;

import bulls.dateTime.DateCenter;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.hephaestus.collection.enums.LpCalendarType;
import bulls.log.DefaultLogger;
import org.bson.Document;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public enum ExpiryMaster implements EarlyInitialize {
    Instance;
    final ArrayList<LpCalendar> monthlyPeriod = new ArrayList<>();
    final ArrayList<LpCalendar> quarterlyPeriod = new ArrayList<>();
    final ArrayList<LpCalendar> monthlyVkospiPeriod = new ArrayList<>();

    ExpiryMaster() {
        Document query = new Document("dateType", "KRX파생");
        var col = DBCenter.Instance.findIterable(MongoDBDBName.MANAGE_DATA, MongoDBCollectionName.CALENDAR, query);
        for (Document d : col) {
            Integer beginInt = d.getInteger("beginDate");
            Integer endInt = d.getInteger("endDate");
            String target = d.getString("target");

            if (beginInt == null || endInt == null)
                continue;
            try {
                Date dateBegin = DateCenter.Instance.parse_yyyyMMdd(beginInt.toString());
                LocalDate ldBegin = TimeCenter.getDateAsLocalDateType(dateBegin);
                Date dateEnd = DateCenter.Instance.parse_yyyyMMdd(endInt.toString());
                LocalDate ldEnd = TimeCenter.getDateAsLocalDateType(dateEnd);

                String dateSubType = d.getString("dateSubType");

                switch (dateSubType) {
                    case "M" ->
                            monthlyPeriod.add(new LpCalendar(LpCalendarType.EXPIRY, dateSubType, target, ldBegin, ldEnd));
                    case "Q" ->
                            quarterlyPeriod.add(new LpCalendar(LpCalendarType.EXPIRY, dateSubType, target, ldBegin, ldEnd));
                    case "M_Vkospi" ->
                            monthlyVkospiPeriod.add(new LpCalendar(LpCalendarType.EXPIRY, dateSubType, target, ldBegin, ldEnd));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                DefaultLogger.logger.error("Failed to process {}", d);
            }
        }
    }

    public LpCalendar getMonthlyCalendar(LocalDate date) {
        for (LpCalendar lpCalendar : monthlyPeriod) {
            if (lpCalendar.contains(date))
                return lpCalendar;
        }
        return null;
    }

    public LpCalendar getMonthlyVkospiCalendar(LocalDate date) {
        for (LpCalendar lpCalendar : monthlyVkospiPeriod) {
            if (lpCalendar.contains(date))
                return lpCalendar;
        }
        return null;
    }

    public LpCalendar getQuarterlyCalendar(LocalDate date) {
        for (LpCalendar lpCalendar : quarterlyPeriod) {
            if (lpCalendar.contains(date))
                return lpCalendar;
        }
        return null;
    }

    public LpCalendar getNextMonthlyCalendar(LocalDate date) {
        return getCalendar(monthlyPeriod, date);
    }

    public LpCalendar getNextQuarterlyCalendar(LocalDate date) {
        return getCalendar(quarterlyPeriod, date);
    }

    private LpCalendar getCalendar(List<LpCalendar> period, LocalDate date) {
        for (int i = 0; i < period.size(); ++i) {
            if (period.get(i).contains(date)) {
                if (i == period.size() - 1)
                    return null;
                else
                    return period.get(i + 1);
            }

        }

        return null;
    }
}