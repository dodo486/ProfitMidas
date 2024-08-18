package bulls.db.influxdb;


import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.thread.GeneralCoreThread;
import bulls.tool.util.GeneralCoreTimer;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class MongoToInfluxRecorder implements Subscriber<ChangeStreamDocument<Document>> {
    public final LocalTime startTime, endTime;
    public final MongoToInfluxTransform transform;

    private LocalTime nextTime;
    private GeneralCoreTimer timer;

    private Subscription subscription;

    public MongoToInfluxRecorder(LocalTime startTime, LocalTime endTime, MongoToInfluxTransform transform) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.transform = transform;
    }

    public void start() {
        stop();
        nextTime = LocalTime.MIN;
        timer = getRecordTimer(transform);
    }


    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public GeneralCoreTimer getRecordTimer(MongoToInfluxTransform transform) {
        String threadName = transform.bucket + "_" + transform.measurement;

        Thread t = new GeneralCoreThread(threadName, () -> {
            LocalTime currentTime = TimeCenter.Instance.getDateTimeAsLocalDateTimeType().toLocalTime();
            if (currentTime.compareTo(startTime) < 0 || currentTime.compareTo(endTime) > 0)
                return;

            if (currentTime.compareTo(nextTime) >= 0) {
                nextTime = currentTime.withNano(0).plusSeconds(transform.intervalSec);
                Instant i = LocalDateTime.of(TimeCenter.Instance.today, currentTime.withNano(0)).atZone(ZoneId.systemDefault()).toInstant();

                List<Point> pointList = new ArrayList<>();

                MongoCursor<Document> cursor = DBCenter.Instance.find(transform.dbName, transform.collectionName, Document.class);
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    Point p = Point.measurement(transform.measurement).time(i, WritePrecision.NS);
                    boolean isValid = true;

                    for (var tagName : transform.tagList) {
                        String tag = doc.getString(tagName);
                        if (tag == null) {
                            System.out.println("Document에 " + tagName + " tag가 없습니다. doc=" + doc);
                            isValid = false;
                            break;
                        }

                        p.addTag(tagName, tag);
                    }

                    for (var fieldName : transform.fieldList) {
                        Object field = doc.get(fieldName);
                        if (field == null) {
                            System.out.println("Document에 " + fieldName + " field가 없습니다. doc=" + doc);
                            isValid = false;
                            break;
                        }

                        Class<?> clazz = field.getClass();

                        if (clazz == Integer.class || clazz == Short.class) {
                            p.addField(fieldName, (Integer) field);
                        } else if (clazz == Double.class || clazz == Float.class) {
                            p.addField(fieldName, (Double) field);
                        } else if (clazz == Long.class) {
                            p.addField(fieldName, (Long) field);
                        } else if (clazz == Boolean.class) {
                            p.addField(fieldName, (Boolean) field);
                        } else if (clazz == String.class) {
                            p.addField(fieldName, (String) field);
                        } else {
                            System.out.println("Document에 " + fieldName + " field는 지원하지 않는 값입니다. doc=" + doc);
                            isValid = false;
                            break;
                        }
                    }

                    if (!isValid)
                        continue;

                    pointList.add(p);
                }

                if (pointList.size() == 0)
                    return;

                PointListWrapper wrapper = new PointListWrapper(transform.bucket, pointList);
                InfluxDBDataUploadCenter.Instance.upload(wrapper);
            }
        });

        GeneralCoreTimer timer = new GeneralCoreTimer(threadName);
        timer.scheduleAtFixedRate(t, transform.intervalSec * 1000L, transform.intervalSec * 333L);
        return timer;
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(ChangeStreamDocument<Document> documentChangeStreamDocument) {
        Document newDoc = documentChangeStreamDocument.getFullDocument();

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
