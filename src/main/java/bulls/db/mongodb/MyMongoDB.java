package bulls.db.mongodb;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import bulls.log.DefaultLogger;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

// MyMongoDB를 사용하는 부분을 모두 DBCenter로 전환하고 MyMongoDB는 제거해야 함
@Deprecated
public enum MyMongoDB {
    Instance;

    private final UpdateOptions upsert = new UpdateOptions().upsert(true);
    private final CodecRegistry pojoCodecRegistry;
    private final MongoClient client;

    MyMongoDB() {
        pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        DefaultLogger.logger.info("MyMongoDB DB_URI : {}", MongoDBUri.Instance.getUri());
        client = new MongoClient(new MongoClientURI(MongoDBUri.Instance.getUri()));
    }

    @Deprecated
    public DBCollection getCollection(String dbName, String collectionName) {
        DB database = client.getDB(dbName);
        return database.getCollection(collectionName);
    }

    @Deprecated
    public MongoCollection<Document> getMongoCollection(String dbName, String collectionName) {
        MongoDatabase database = client.getDatabase(dbName);
        return database.getCollection(collectionName);
    }

    @Deprecated
    public <T> MongoCollection<T> getMongoCollection(String dbName, String collectionName, Class<T> type) {
        return client.getDatabase(dbName).getCollection(collectionName, type).withCodecRegistry(pojoCodecRegistry);
    }
}
