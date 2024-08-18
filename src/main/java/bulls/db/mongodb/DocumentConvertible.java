package bulls.db.mongodb;

import org.bson.Document;

public interface DocumentConvertible {
    Document getDataDocument();

    Document getQueryDocument();
}
