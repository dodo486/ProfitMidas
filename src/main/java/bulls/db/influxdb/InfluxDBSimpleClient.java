package bulls.db.influxdb;

import bulls.log.DefaultLogger;
import bulls.server.ServerMessageSender;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;

import java.util.List;

public class InfluxDBSimpleClient {
    private static final String DEFAULT_INFLUXDB_URL = "http://172.28.203.110:8086";
    private static final String DEFAULT_INFLUXDB_TOKEN = "F4fzM_5GSdSrfpRKEQEH4TSinLPNat_6SjAH_tHkjgdEIktjoJa8YeGzUG8U4jprCWDtR07R7kwvUk9hqCwcNw==";

    private static final int MAX_RECONNECT_COUNT = 15;

    private final String dbUrl;
    private final String dbToken;

    private InfluxDBClientStatus status = InfluxDBClientStatus.DISCONNECTED;
    private InfluxDBClient client = null;
    private WriteApiBlocking writeApi = null;

    public InfluxDBSimpleClient() {
        this.dbUrl = DEFAULT_INFLUXDB_URL;
        this.dbToken = DEFAULT_INFLUXDB_TOKEN;
    }

    public InfluxDBSimpleClient(String dbUrl, String dbToken) {
        this.dbUrl = dbUrl;
        this.dbToken = dbToken;
    }

    public void start() {
        if (status != InfluxDBClientStatus.DISCONNECTED) {
            DefaultLogger.logger.error("InfluxDBSimpleClient가 이미 시작한 상태입니다.");
            return;
        }

        client = InfluxDBClientFactory.create(dbUrl, dbToken.toCharArray());
        client.enableGzip();
        writeApi = client.getWriteApiBlocking();
        status = InfluxDBClientStatus.CONNECTED;
    }

    private void reconnect() {
        this.status = InfluxDBClientStatus.TRYING_TO_RECONNECT;
        DefaultLogger.logger.error("InfluxDB와 연결이 끊겼습니다.");
        ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "알림", "InfluxDB와 연결이 끊겼습니다.");
        int reconnectCount = 0;
        while (reconnectCount <= MAX_RECONNECT_COUNT && status == InfluxDBClientStatus.TRYING_TO_RECONNECT) {
            try {
                reconnectCount++;
                DefaultLogger.logger.error("20초 후 InfluxDB와 다시 연결을 시도합니다. 현재 재시도 횟수={}", reconnectCount);
                Thread.sleep(20_000);

                // 기존 연결을 끊고 다시 연결
                client.close();
                client = InfluxDBClientFactory.create(dbUrl, dbToken.toCharArray());
                client.enableGzip();
                writeApi = client.getWriteApiBlocking();

                DefaultLogger.logger.info("InfluxDB와 다시 연결되었습니다.");
                ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "알림", "InfluxDB와 다시 연결되었습니다.");
                status = InfluxDBClientStatus.CONNECTED;
                break;
            } catch (InterruptedException e) {
                DefaultLogger.logger.error("InfluxDB 재연결 실패! 20초 후 다시 연결합니다.");
            }
        }

        if (status == InfluxDBClientStatus.TRYING_TO_RECONNECT) {
            DefaultLogger.logger.error("InfluxDB와의 연결을 복원할 수 없습니다. 문제 해결 후 재시작이 필요합니다.");
            ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "알림", "InfluxDB와의 연결을 복원할 수 없습니다. 문제 해결 후 재시작이 필요합니다.");
            status = InfluxDBClientStatus.FAILED;
        }
    }

    public void stop() {
        if (status == InfluxDBClientStatus.DISCONNECTED) {
            DefaultLogger.logger.error("InfluxDBSimpleClient가 이미 종료된 상태입니다.");
            return;
        }

        client.close();
        status = InfluxDBClientStatus.DISCONNECTED;
    }

    public boolean writePoints(String bucket, String org, List<Point> points) {
        if (status != InfluxDBClientStatus.CONNECTED)
            return false;

        try {
            writeApi.writePoints(bucket, org, points);
            return true;
        } catch (InfluxException e) {
            e.printStackTrace();
            reconnect();
            return false;
        }
    }

    public InfluxDBClientStatus getStatus() {
        return status;
    }
}
