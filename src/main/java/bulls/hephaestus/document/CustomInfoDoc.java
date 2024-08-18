package bulls.hephaestus.document;

import bulls.db.mongodb.DocumentConvertible;
import bulls.json.DefaultMapper;
import org.bson.Document;

import java.util.Date;

public final class CustomInfoDoc implements DocumentConvertible {


//    public CustomInfoDoc(){
//
//    }

    public String representingCode; //사람이 알아먹을수 있는 코드, alias 로 쓰여 strategyCode 와 대응
    public String installerClassName;
    public String strategyCode;
    public Document installerInstance;
    public Date lastUpdate;

    @Override
    public Document getDataDocument() {
        String json = DefaultMapper.getGMapper().toJson(this);
        Document doc = Document.parse(json);
        doc.put("lastUpdate", lastUpdate);

        return doc;
    }

    @Override
    public Document getQueryDocument() {
        return new Document("representingCode", representingCode);
    }
}
