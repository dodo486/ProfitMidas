package bulls.dmaLog.transactiontracker;

import com.influxdb.client.write.Point;
import bulls.db.influxdb.InfluxDBDataUploadCenter;
import bulls.db.influxdb.PointListWrapper;
import bulls.designTemplate.observer.Observer;
import bulls.dmaLog.DMALog;
import bulls.dmaLog.DMALogList;
import bulls.dmaLog.TradeDMALog;

import java.util.ArrayList;
import java.util.List;

public enum FEPLogTSDBLiveUploader implements Observer<DMALogList> {
    Instance;

    FEPLogTSDBLiveUploader() {
    }

    public void start() {
        InfluxDBDataUploadCenter.Instance.start();
    }

    public void stop() {
        InfluxDBDataUploadCenter.Instance.stop();
    }

    @Override
    public void update(DMALogList data) {
        List<Point> pointList = new ArrayList<>();
        for (DMALog log : data.getLogList()) {
            if (log instanceof TradeDMALog) {
                Point p = ((TradeDMALog) log).toPoint();
                pointList.add(p);
            }
        }

        InfluxDBDataUploadCenter.Instance.upload(new PointListWrapper("MM", pointList));
    }
}
