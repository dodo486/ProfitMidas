package bulls.db.mongodb;

public enum MongoDBCollectionInfo implements DBAssigned {
    BOOK_MASTER(MongoDBDBName.BOOK, MongoDBCollectionName.BOOK_MASTER);

    public final String dbName;
    public final String collectionName;

    MongoDBCollectionInfo(String dbName, String collectionName) {
        this.dbName = dbName;
        this.collectionName = collectionName;
    }

    @Override
    public String getDBName() {
        return dbName;
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }
}
