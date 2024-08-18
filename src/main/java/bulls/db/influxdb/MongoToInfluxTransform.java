package bulls.db.influxdb;

import org.bson.Document;

import java.util.List;

public class MongoToInfluxTransform {
    public final String dbName;
    public final String collectionName;
    public final String bucket;
    public final String measurement;
    public final List<String> tagList;
    public final List<String> fieldList;
    public final int intervalSec;
    public final boolean watch;

    public MongoToInfluxTransform(String dbName, String collectionName, String bucket, String measurement, List<String> tagList, List<String> fieldList, int intervalSec, boolean watch) {
        this.dbName = dbName;
        this.collectionName = collectionName;
        this.bucket = bucket;
        this.measurement = measurement;
        this.tagList = tagList;
        this.fieldList = fieldList;
        this.intervalSec = intervalSec;
        this.watch = watch;
    }

    public static MongoToInfluxTransform of(Document doc) {
        String dbName = doc.getString("dbName");
        String collectionName = doc.getString("collectionName");
        String bucket = doc.getString("bucket");
        String measurement = doc.getString("measurement");
        List<String> tagList = doc.getList("tagList", String.class);
        List<String> fieldList = doc.getList("fieldList", String.class);

        int intervalSec = doc.getInteger("intervalSec", -1);
        boolean watch = doc.getBoolean("watch", false);

        if (dbName == null ||
            collectionName == null ||
            bucket == null ||
            measurement == null ||
            tagList == null || tagList.size() == 0 ||
            fieldList == null || fieldList.size() == 0 ||
            intervalSec < 0) {
            System.out.println("데이터가 잘못되어 MongoToInfluxTransform 생성에 실패했습니다. doc=" + doc);
            return null;
        }

        return new MongoToInfluxTransform(dbName, collectionName, bucket, measurement, tagList, fieldList, intervalSec, watch);
    }
}
