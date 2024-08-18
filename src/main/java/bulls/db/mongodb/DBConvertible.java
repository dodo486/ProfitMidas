package bulls.db.mongodb;

import com.mongodb.DBObject;

@Deprecated
public interface DBConvertible {
    @Deprecated
    DBObject toDBObject();

    @Deprecated
    DBObject query();
}
