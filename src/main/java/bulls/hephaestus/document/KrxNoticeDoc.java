package bulls.hephaestus.document;

import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.mongodb.MongoDBData;
import bulls.json.DefaultMapper;
import org.bson.Document;

import java.util.Date;

public class KrxNoticeDoc implements MongoDBData {
    public String trCode;
    public String isinCode;
    public String productName;
    public int noticeIndex;
    public int noticeDate;
    public String marketType;
    public String title;
    public Date lastUpdate;

    @Override
    public String getDBName() {
        return MongoDBDBName.NOTICE;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.KRX_NOTICE;
    }

    @Override
    public Document getDataDocument() {
        String json = DefaultMapper.getGMapper().toJson(this);
        return Document.parse(json);
    }

    @Override
    public Document getQueryDocument() {
        return new Document("noticeIndex", noticeIndex);
    }

    @Override
    public String toString() {
        return "KrxNoticeDoc{" +
                "trCode='" + trCode + '\'' +
                ", isinCode='" + isinCode + '\'' +
                ", productName='" + productName + '\'' +
                ", noticeIndex=" + noticeIndex +
                ", noticeDate=" + noticeDate +
                ", marketType='" + marketType + '\'' +
                ", title='" + title + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
