package bulls.hephaestus.collection;

import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.hephaestus.document.BookMasterDoc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public enum BookMaster {
    Instance;

    private final HashMap<String, BookMasterDoc> map = new HashMap<>();

    private final HashMap<String, String> singleStockIsinToMBookCodeMap = new HashMap<>();
    private final HashMap<String, String> singleStockIsinToSBookCodeMap = new HashMap<>();
    private final List<String> offDutyBookCodeList = new ArrayList<>();

    BookMaster() {
        var col = DBCenter.Instance.findIterable(MongoDBDBName.BOOK, MongoDBCollectionName.BOOK_MASTER, BookMasterDoc.class);
        for (BookMasterDoc d : col) {
            map.put(d.bookCode, d);

            if (d.mainSub.equals("M") && d.bookType.equals("singleStock"))
                singleStockIsinToMBookCodeMap.put(d.primaryIsinCode, d.bookCode);
            else if (d.mainSub.equals("S") && d.bookType.equals("singleStock"))
                singleStockIsinToSBookCodeMap.put(d.primaryIsinCode, d.bookCode);

            if (d.mainSub.equals("M") && d.bookType.equals("indexSF"))
                offDutyBookCodeList.add(d.bookCode);
        }

    }

    public BookMasterDoc getBookMasterDoc(String bookCode) {
        return map.get(bookCode);
    }

    public String getBookName(String bookCode) {
        BookMasterDoc d = map.get(bookCode);
        if (d == null) {
            return bookCode;
        }
        return d.bookName;
    }

    public String getSingleStockMainBookCodeOf(String isinCode) {
        return singleStockIsinToMBookCodeMap.get(isinCode);
    }

    public String getSingleStockSubBookCodeOf(String isinCode) {
        return singleStockIsinToSBookCodeMap.get(isinCode);
    }

    public boolean isFinalLpDutyDate(String bookCode, LocalDate date) {
        if (offDutyBookCodeList.contains(bookCode))
            return false;

        BookMasterDoc doc = map.get(bookCode);
        LpCalendar calendar;
        if (doc.expiryCycle == 3) {
            calendar = LPDutyMaster.Instance.getQuarterlyCalendar(date);
        } else if (doc.expiryCycle == 1 && !doc.bookType.equals("vkospi")) {
            calendar = LPDutyMaster.Instance.getMonthlyCalendar(date);
        } else if (doc.expiryCycle == 1 && doc.bookType.equals("vkospi")) {
            calendar = LPDutyMaster.Instance.getMonthlyVkospiCalendar(date);
        } else {
            return false;
        }

        return calendar.isEnd(date);
    }

    public boolean isFinalLpTradeDate(String bookCode, LocalDate date) {
        BookMasterDoc doc = map.get(bookCode);
        LpCalendar expiryCalendar;

        if (doc.bookCode.equals("M:STOCKLP") || doc.bookCode.equals("M:SUB_STOCKLP")) {
            expiryCalendar = ExpiryMaster.Instance.getMonthlyCalendar(date);
            return expiryCalendar.isEnd(date);
        }

        if (doc.bookType.equals("vkospi")) {
            expiryCalendar = ExpiryMaster.Instance.getMonthlyVkospiCalendar(date);
            return expiryCalendar.isEnd(date);
        }

        if (doc.expiryCycle == 3) {
            expiryCalendar = ExpiryMaster.Instance.getQuarterlyCalendar(date);
        } else if (doc.expiryCycle == 1) {
            expiryCalendar = ExpiryMaster.Instance.getMonthlyCalendar(date);
        } else {
            return false;
        }

        return expiryCalendar.isEnd(date);
    }

    public boolean isMainBook(String bookCode) {
        BookMasterDoc doc = map.get(bookCode);
        if (doc == null)
            return false;
        return doc.mainSub.equals("M");
    }

    public String getSubBookOf(String mainBookCode) {
        BookMasterDoc doc = map.get(mainBookCode);
        if (doc == null)
            return null;

        String subBookCode = "S" + mainBookCode.substring(1);
        BookMasterDoc subDoc = map.get(subBookCode);
        if (subDoc == null)
            return null;
        return subBookCode;
    }

    public Collection<BookMasterDoc> getAllBookMasterDoc() {
        return map.values();
    }
}
