package bulls.hephaestus.document;


import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DocumentConvertible;
import bulls.json.DefaultMapper;
import bulls.staticData.AliasManager;
import org.bson.Document;

import java.util.Date;

public final class StockClearingOnExpiryDoc implements DocumentConvertible {
    public StockClearingOnExpiryDoc(String account, String code, long 일반, long 대차, long 부족, long 잔여매도가능수량) {
        this.account = account;
        this.isinCode = code;
        this.shortCode = code.substring(3, 9);
        this.normalQty = 일반;
        this.loanQty = 대차;
        this.lackQty = 부족;
        this.availableShortQty = 잔여매도가능수량;
        this.lastUpdate = TimeCenter.Instance.todayDate;
    }

    public final String isinCode;
    public final String account;
    public final String shortCode;
    public final long normalQty;
    public final long loanQty;
    public final long lackQty;
    public final long availableShortQty;
    public final Date lastUpdate;

    public String toString() {
        return String.format("%s %s %s %s %s %s %s", AliasManager.Instance.getKoreanFromIsin(isinCode), account, shortCode, normalQty, loanQty, lackQty, availableShortQty);
    }

    @Override
    public Document getDataDocument() {
        String json = DefaultMapper.getGMapper().toJson(this);
        Document doc = Document.parse(json);
        doc.put("lastUpdate", lastUpdate);

        return doc;
    }

    @Override
    public Document getQueryDocument() {
        return new Document()
                .append("account", account)
                .append("isinCode", isinCode);
    }
}