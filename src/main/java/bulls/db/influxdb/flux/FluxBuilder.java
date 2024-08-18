package bulls.db.influxdb.flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;

public class FluxBuilder {
    private StringBuilder flux;

    public FluxBuilder(String bucket, LocalDate start, LocalDate stop) {
        init(bucket, start, stop, LocalTime.MIN, LocalTime.MAX);
    }

    public FluxBuilder(String bucket, LocalDateTime start, LocalDateTime stop) {
        init(bucket, start, stop);
    }

    public FluxBuilder(String bucket, LocalDate date, boolean marketTimeOnly) {
        LocalTime startTime, endTime;
        if (marketTimeOnly) {
            startTime = LocalTime.of(9, 0, 0);
            endTime = LocalTime.of(15, 20, 0);
        } else {
            startTime = LocalTime.MIN;
            endTime = LocalTime.MAX;
        }
        init(bucket, date, date, startTime, endTime);
    }

    private void init(String bucket, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDateTime end = LocalDateTime.of(endDate, endTime);
        init(bucket, start, end);
    }

    private void init(String bucket, LocalDateTime start, LocalDateTime end) {
        flux = new StringBuilder();
        flux.append(FluxUtil.getBaseString(bucket, start, end));
    }

    public FluxBuilder addFilterString(String key, Collection<?> values) {
        flux.append(FluxUtil.getFilterString(key, values));
        return this;
    }

    public FluxBuilder addFilterByKeyValue(String key, Object value) {
        flux.append(FluxUtil.getFilterStringByKeyValue(key, value));
        return this;
    }

    public FluxBuilder addAggregate(AggregateParameters params) {
        flux.append(params);
        return this;
    }

    public FluxBuilder addPivot() {
        flux.append(FluxUtil.getDropString(null));
        return this;
    }

    public FluxBuilder addCustom(String customQuery) {
        flux.append(customQuery).append("\n");
        return this;
    }

    public FluxBuilder addDrop(Collection<String> columns) {
        flux.append(FluxUtil.getDropString(columns));
        return this;
    }

    public FluxBuilder addRename(String newColumnName) {
        flux.append(FluxUtil.getRenameString(newColumnName));
        return this;
    }

    public String build() {
        return flux.toString();
    }
}
