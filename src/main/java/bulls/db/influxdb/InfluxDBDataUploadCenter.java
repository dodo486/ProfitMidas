package bulls.db.influxdb;

import bulls.designTemplate.EarlyInitialize;
import bulls.designTemplate.UniqueStringPair;
import bulls.log.DefaultLogger;
import bulls.thread.GeneralCoreThread;
import bulls.tool.util.MultiScheduleTimer;
import bulls.tool.util.PeriodicRunnable;
import com.influxdb.client.write.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public enum InfluxDBDataUploadCenter implements EarlyInitialize {
    Instance;

    private final BlockingQueue<PointListWrapper> dataQueue;

    private final ConcurrentHashMap<UniqueStringPair, ConcurrentLinkedQueue<Point>> pointQueueMap;
    private Thread updateThread;
    private final PeriodicRunnable uploadPeriodicRunnable;
    private final PointListWrapper POISON;

    private final InfluxDBSimpleClient client;

    private final int UPLOAD_PERIOD_SEC = 15;

    InfluxDBDataUploadCenter() {
        dataQueue = new LinkedBlockingQueue<>();
        pointQueueMap = new ConcurrentHashMap<>();
        POISON = new PointListWrapper(null);
        client = new InfluxDBSimpleClient();
        uploadPeriodicRunnable = new PeriodicRunnable("InfluxDBDataUploadCenter_Upload", this::uploadData, UPLOAD_PERIOD_SEC, 0);
        start();
    }

    public void start() {
        client.start();
        updateThread = new GeneralCoreThread("InfluxDBDataUploadCenter_Update", this::updateData);
        updateThread.start();
        MultiScheduleTimer.Instance.registerPeriodic(uploadPeriodicRunnable);
    }

    public void stop() {
        if (updateThread != null) {
            dataQueue.add(POISON);

            try {
                updateThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            updateThread = null;
        }

        MultiScheduleTimer.Instance.unRegisterPeriodic(uploadPeriodicRunnable);
        client.stop();

        // 혹시나 데이터가 남아있을 수 있으므로 마지막 업로드
        uploadData();
    }

    public void upload(PointListWrapper wrapper) {
        if (updateThread == null) {
            System.out.println("uploadThread가 null이므로 데이터를 받지 않습니다.");
            return;
        }

        dataQueue.add(wrapper);
    }

    private void uploadData() {
        for (var entry : pointQueueMap.entrySet()) {
            var bucketOrgPair = entry.getKey();
            var dataQueue = entry.getValue();

            String bucket = bucketOrgPair.first;
            String org = bucketOrgPair.second;

            List<Point> pointList = new ArrayList<>();
            // 일단은 큐가 빌 때까지 데이터를 다 뺀다.
            // 혹시나 다른 쪽 starvation이 일어나지는 않는지 확인 필요
            while (!dataQueue.isEmpty())
                pointList.add(dataQueue.poll());

            if (pointList.size() == 0)
                continue;

            boolean success = client.writePoints(bucket, org, pointList);
            if (success) {
                DefaultLogger.logger.info("InfluxDB Upload Complete! bucket={}, org={}, point_list_size={}",
                        bucket, org, pointList.size());
            } else {
                // 업로드에 실패한 데이터는 다시 큐에 넣는다.
                dataQueue.addAll(pointList);
                DefaultLogger.logger.info("InfluxDB Upload Error! bucket={}, org={}, bucket_queue_size={}",
                        bucket, org, dataQueue.size());
            }
        }
    }

    private void updateData() {
        while (true) {
            try {
                var data = dataQueue.take();
                if (data == POISON)
                    break;

                if (data.getPointList() == null || data.getPointList().size() == 0)
                    continue;

                var bucketOrgPair = UniqueStringPair.getOrCreate(data.getBucket(), data.getOrg());
                pointQueueMap.computeIfAbsent(bucketOrgPair, bao -> new ConcurrentLinkedQueue<>()).addAll(data.getPointList());

                if (dataQueue.size() >= 1_000) {
                    DefaultLogger.logger.info("InfluxDBDataUploadCenter dataQueue Warning! data_queue_size={}", dataQueue.size());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
