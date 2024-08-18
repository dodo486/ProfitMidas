package bulls.db.influxdb;

import bulls.dateTime.TimeCenter;
import com.influxdb.client.write.Point;

import java.time.LocalTime;
import java.util.List;

public final class PointListWrapper {
    private final List<Point> pointList;
    private final String org;
    private final String bucket;

    private final long epoch;

    public PointListWrapper(List<Point> pointList) {
        this.org = "MM";
        this.bucket = "MM";
        this.pointList = pointList;
        epoch = TimeCenter.Instance.getTime();
    }

    public PointListWrapper(String bucket, List<Point> pointList) {
        this.org = "MM";
        this.bucket = bucket;
        this.pointList = pointList;
        epoch = TimeCenter.Instance.getTime();
    }

    public PointListWrapper(String org, String bucket, List<Point> pointList) {
        this.org = org;
        this.bucket = bucket;
        this.pointList = pointList;
        epoch = TimeCenter.Instance.getTime();
    }

    public String getOrg() {
        return org;
    }

    public String getBucket() {
        return bucket;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    public long getEpoch() {
        return epoch;
    }

    public LocalTime getTime() {
        return TimeCenter.getEpochAsLocalDateTimeType(epoch).toLocalTime();
    }
}
