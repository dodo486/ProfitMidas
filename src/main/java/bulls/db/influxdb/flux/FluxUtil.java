package bulls.db.influxdb.flux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public abstract class FluxUtil {

    public static String getBaseString(String bucket, LocalDateTime start, LocalDateTime stop) {
        return "from(bucket: \"" + bucket + "\")\n|> range(start: " + getTimeString(start) + ", stop: " + getTimeString(stop) + ")\n";
    }

    private static String getTimeString(LocalDateTime t) {
        return t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "T" + t.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "+09:00";
    }

    public static String getFilterStringByKeyValue(String key, Object value) {
        String valueString = value.toString();
        return "|> filter(fn: (r) => r." + key + " == \"" + valueString + "\")\n";
    }

    public static String getFilterString(String key, Collection<?> values) {
        if (values == null || values.size() == 0)
            return "";

        if (values.size() == 1) {
            for (Object value : values)
                return getFilterStringByKeyValue(key, value);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("|> filter(fn: (r) => ");

        for (Object value : values)
            sb.append("r.").append(key).append(" == \"").append(value.toString()).append("\" or ");

        sb.delete(sb.length() - 4, sb.length() - 1);

        sb.append(")\n");
        return sb.toString();
    }

    public static String getDropString(Collection<String> dropColumns) {
        if (dropColumns == null || dropColumns.size() == 0)
            dropColumns = List.of("_start", "_stop", "_measurement");

        StringBuilder sb = new StringBuilder();
        sb.append("|> drop(columns:[");
        for (String column : dropColumns) {
            sb.append("\"").append(column).append("\", ");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("])\n");
        return sb.toString();
    }

    public static String getPivotString() {
        return "|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\")\n";
    }

    public static String getRenameString(String newColumnName) {
        return "|> rename(columns: {{_value: \"" + newColumnName + "\"}})\n";
    }

    // InfluxDB에서 돌려주는 결과는 UTC 시간이므로 한국 시간으로 환산해야 실제 시간이 된다.
    public static LocalDateTime toLocalTime(Instant t) {
        return t.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
