package bulls.db.influxdb;

public enum InfluxDBClientStatus {
    DISCONNECTED,
    CONNECTED,
    TRYING_TO_RECONNECT,
    FAILED
}
