package bulls.hephaestus.document;

import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.mongodb.MongoDBData;
import bulls.json.DefaultMapper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilteredKrxNoticeDoc implements MongoDBData {
    public String isinCode;
    public String productName;
    public int noticeIndex;
    public String noticeDate;
    public String title;
    public Date lastUpdate;

    public String index;
    public String memo;
    public boolean ignore;
    public String lastEditor;
    public List<String> checker;
    public String action1, action2, action3;
    public Date actDate1, actDate2, actDate3;

    @Override
    public String getDBName() {
        return MongoDBDBName.NOTICE;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.FILTERED_KRX_NOTICE;
    }

    @Override
    public Document getDataDocument() {
        String json = DefaultMapper.getGMapper().toJson(this);
        Document doc = Document.parse(json);
        doc.put("lastUpdate", new Date());
        if (actDate1 == null)
            doc.put("actDate1", "");
        if (actDate2 == null)
            doc.put("actDate2", "");
        if (actDate3 == null)
            doc.put("actDate3", "");
        return doc;
//        return Document.parse(json);
    }

    @Override
    public Document getQueryDocument() {
        return new Document("noticeIndex", noticeIndex);
    }

    public static FilteredKrxNoticeDoc of(KrxNoticeDoc rawDoc) {
        FilteredKrxNoticeDoc doc = new FilteredKrxNoticeDoc();
        doc.isinCode = rawDoc.isinCode;
        doc.productName = rawDoc.productName;
        doc.noticeIndex = rawDoc.noticeIndex;
        doc.noticeDate = "" + rawDoc.noticeDate;
        doc.title = rawDoc.title;
        doc.ignore = false;
        doc.checker = new ArrayList<>();
        doc.memo = doc.action1 = doc.action2 = doc.action3 = doc.lastEditor = "";

        return doc;
    }

    @Override
    public String toString() {
        return "FilteredKrxNoticeDoc{" +
                "isinCode='" + isinCode + '\'' +
                ", productName='" + productName + '\'' +
                ", noticeIndex=" + noticeIndex +
                ", noticeDate=" + noticeDate +
                ", title='" + title + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", index='" + index + '\'' +
                ", memo='" + memo + '\'' +
                ", ignore=" + ignore +
                ", lastEditor='" + lastEditor + '\'' +
                ", checker=" + checker +
                ", action1='" + action1 + '\'' +
                ", action2='" + action2 + '\'' +
                ", action3='" + action3 + '\'' +
                ", actDate1=" + actDate1 +
                ", actDate2=" + actDate2 +
                ", actDate3=" + actDate3 +
                '}';
    }
}
