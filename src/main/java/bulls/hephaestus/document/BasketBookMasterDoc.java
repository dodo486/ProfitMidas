package bulls.hephaestus.document;

import bulls.db.mongodb.DocumentConvertible;
import bulls.json.DefaultMapper;
import org.bson.Document;

import java.util.Date;
import java.util.Set;

public class BasketBookMasterDoc implements DocumentConvertible {

    public String bookCode;
    public String bookName;
    public String bookType;
    public String mainSub = "M";
    public String primaryIsinCode;
    public String duType;
    public Set<String> pTypeSet;
    public Date lastUpdate;
    public int expiryCycle;

    @Override
    public Document getDataDocument() {
        String json = DefaultMapper.getGMapper().toJson(this);
        Document doc = Document.parse(json);
        doc.put("lastUpdate", lastUpdate);

        return doc;
    }

    @Override
    public Document getQueryDocument() {
        return new Document("bookCode", bookCode);
    }
}
