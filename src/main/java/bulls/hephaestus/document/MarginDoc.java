package bulls.hephaestus.document;

import bulls.db.mongodb.DocumentConvertible;
import org.bson.Document;

import java.util.Date;

public class MarginDoc implements DocumentConvertible {
    public String account;
    public double 총한도;
    public double 사용한도;
    public double percentage;
    public Date lastUpdate;

    @Override
    public Document getQueryDocument() {
        return new Document("account", account);
    }

    @Override
    public Document getDataDocument() {
        return new Document()
                .append("account", account)
                .append("총한도", 총한도)
                .append("사용한도", 사용한도)
                .append("percentage", percentage)
                .append("lastUpdate", lastUpdate);
    }
}
