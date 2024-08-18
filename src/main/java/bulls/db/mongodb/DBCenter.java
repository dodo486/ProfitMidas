package bulls.db.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import bulls.datastructure.Pair;
import bulls.designTemplate.EarlyInitialize;
import bulls.log.DefaultLogger;
import bulls.thread.GeneralCoreThread;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public enum DBCenter implements EarlyInitialize {
    Instance;

    final Logger feedToDBLogger;

    private final ConcurrentHashMap<String, BlockingQueue<Pair<Document, Document>>> queueMap;
    private final ConcurrentHashMap<String, Thread> threadMap;

    private static final ReplaceOptions upsertTrue = new ReplaceOptions().upsert(true);
    private static final BulkWriteOptions bulkOrderFalse = new BulkWriteOptions().ordered(false);
    private final CodecRegistry pojoCodecRegistry;
    private boolean isFinished = false;

    DBCenter() {
        DefaultLogger.logger.info("DBCenter DB_URI : {}", MongoDBUri.Instance.getUri());
        client = new MongoClient(new MongoClientURI(MongoDBUri.Instance.getUri()));
        queueMap = new ConcurrentHashMap<>();
        threadMap = new ConcurrentHashMap<>();
        feedToDBLogger = LoggerFactory.getLogger("FeedToDB_DBWrite");
        pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    private MongoClient client;

    public void setServer(String uri) {
        client = new MongoClient(new MongoClientURI(uri));
    }

    MongoCollection<Document> getCollection(String dbName, String collectionName) {
        MongoDatabase database = client.getDatabase(dbName);
        return database.getCollection(collectionName);
    }

    <TDocument> MongoCollection<TDocument> getCollection(String dbName, String collectionName, Class<TDocument> documentClass) {
        MongoDatabase database = client.getDatabase(dbName);
        return database.getCollection(collectionName, documentClass).withCodecRegistry(pojoCodecRegistry);
    }

    public <TDocument> MongoCursor<TDocument> find(String dbName, String collectionName, Class<TDocument> documentClass, Bson filter) {
        MongoCollection<TDocument> col = getCollection(dbName, collectionName, documentClass);
        return col.find(filter).iterator();
    }

    public <TDocument> MongoCursor<TDocument> find(String dbName, String collectionName, Class<TDocument> documentClass) {
        MongoCollection<TDocument> col = getCollection(dbName, collectionName, documentClass);
        return col.find().iterator();
    }

    public MongoCursor<Document> find(String dbName, String collectionName, Bson filter) {
        MongoCollection<Document> col = getCollection(dbName, collectionName, Document.class);
        return col.find(filter).iterator();
    }

    public MongoCursor<Document> find(String dbName, String collectionName) {
        MongoCollection<Document> col = getCollection(dbName, collectionName, Document.class);
        return col.find().iterator();
    }

    public <TDocument> FindIterable<TDocument> findIterable(String dbName, String collectionName, Class<TDocument> documentClass, Bson filter) {
        MongoCollection<TDocument> col = getCollection(dbName, collectionName, documentClass);
        return col.find(filter);
    }

    public <TDocument> FindIterable<TDocument> findIterable(String dbName, String collectionName, Class<TDocument> documentClass) {
        MongoCollection<TDocument> col = getCollection(dbName, collectionName, documentClass);
        return col.find();
    }

    public FindIterable<Document> findIterable(String dbName, String collectionName, Bson filter) {
        MongoCollection<Document> col = getCollection(dbName, collectionName, Document.class);
        return col.find(filter);
    }

    public FindIterable<Document> findIterable(String dbName, String collectionName) {
        MongoCollection<Document> col = getCollection(dbName, collectionName, Document.class);
        return col.find();
    }

    public void waitTillWorkThreadFinish() {
        DefaultLogger.logger.info("Exiting DBCenter...");
        isFinished = true;
        for (Thread t : threadMap.values()) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public long removeAll(String dbName, String collectionName) {
        Document query = new Document();
        MongoCollection<Document> col = getCollection(dbName, collectionName);
        DeleteResult dr = col.deleteMany(query);
        return dr.getDeletedCount();
    }

    public long removeAll(String dbName, String collectionName, Document query) {
        MongoCollection<Document> col = getCollection(dbName, collectionName);
        DeleteResult dr = col.deleteMany(query);
        DefaultLogger.logger.info("Removed {} obj(s)", dr.getDeletedCount());
        return dr.getDeletedCount();
    }

    public long removeOne(String dbName, String collectionName, Document query) {
        MongoCollection<Document> col = getCollection(dbName, collectionName);
        DeleteResult dr = col.deleteOne(query);
        DefaultLogger.logger.info("Removed {} obj(s)", dr.getDeletedCount());
        return dr.getDeletedCount();
    }

    public long update(String dbName, String collectionName, Document query, Document document) {
        MongoCollection<Document> col = getCollection(dbName, collectionName);
        UpdateResult ur = col.replaceOne(query, document, upsertTrue);
        //DefaultLogger.logger.info("Matched : {} Modified : {} Upserted: {}",ur.getMatchedCount(),ur.getModifiedCount(),ur.getUpsertedId());
        return ur.getModifiedCount();
    }

    public void updateBulk(Collection<? extends MongoDBData> dbDataCollection) {
        for (MongoDBData data : dbDataCollection)
            updateBulk(data);
    }

    public void updateBulk(MongoDBData dbData) {
        updateBulk(dbData.getDBName(), dbData.getCollectionName(), dbData.getQueryDocument(), dbData.getDataDocument());
    }

    public void updateBulk(String dbName, String collectionName, DocumentConvertible dc) {
        updateBulk(dbName, collectionName, dc.getQueryDocument(), dc.getDataDocument());
    }

    public void updateBulk(String dbName, String collectionName, Document query, Document data) {
        if (query == null || data == null)
            return;

        StringBuilder key = new StringBuilder(dbName);
        key.append(",");
        key.append(collectionName);
        BlockingQueue<Pair<Document, Document>> queue = queueMap.computeIfAbsent(key.toString(), (k) -> new LinkedBlockingQueue<>());
        queue.add(new Pair<>(query, data));

        threadMap.computeIfAbsent(key.toString(), k -> {
            Thread t = new GeneralCoreThread(key.toString(), () -> {
                DefaultLogger.logger.info("DBCenter " + key + " Started");
                MongoDatabase database = client.getDatabase(dbName);
                MongoCollection<Document> collection = database.getCollection(collectionName);
                BlockingQueue<Pair<Document, Document>> q = queueMap.get(key.toString());
                while (true) {
                    try {
                        Pair<Document, Document> pair = null;
                        try {
                            pair = q.poll(1, TimeUnit.SECONDS);
                            if (pair == null) {
                                if (isFinished) {
                                    DefaultLogger.logger.info("DBCenter " + key + " Exited!");
                                    return;
                                } else
                                    continue;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ArrayList<Pair<Document, Document>> li = new ArrayList<>();
                        li.add(pair);
                        q.drainTo(li, 19999);
                        //DefaultLogger.logger.debug("Draining queue {}. remaining queue size : {}",key.toString(),q.size());
                        List<WriteModel<Document>> updates = new ArrayList<>();
                        li.forEach(curr -> {
                            if (curr.firstElem != null && curr.firstElem.size() > 0) {
                                updates.add(
                                        new ReplaceOneModel<>(
                                                curr.firstElem,
                                                curr.secondElem,
                                                upsertTrue
                                        )
                                );
                            } else {
                                updates.add(
                                        new InsertOneModel<>(
                                                curr.secondElem
                                        )
                                );
                            }
//                        feedToDBLogger.info(collectionName +"|"+ curr.secondElem.toString());
                        });
                        try {
//                        DefaultLogger.logger.info("Start bulkWrite ");
                            com.mongodb.bulk.BulkWriteResult bulkWriteResult = collection.bulkWrite(updates, bulkOrderFalse);
//                        DefaultLogger.logger.info("Finish bulkWrite ");
                        } catch (com.mongodb.MongoBulkWriteException mbwe) {
                            DefaultLogger.logger.error("DB Write Error {}", mbwe.toString());
                        } catch (org.bson.codecs.configuration.CodecConfigurationException cce) {
                            DefaultLogger.logger.error("DB Write Codec Error {}", cce.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            return t;
        });
    }
}