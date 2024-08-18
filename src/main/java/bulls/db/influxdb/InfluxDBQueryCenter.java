package bulls.db.influxdb;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;

import java.util.List;

public enum InfluxDBQueryCenter {
    Instance;

    public final String INFLUXDB_URL = "http://172.28.203.110:8086";
    public final String INFLUXDB_ORG = "MM";
    private final String INFLUXDB_TOKEN = "F4fzM_5GSdSrfpRKEQEH4TSinLPNat_6SjAH_tHkjgdEIktjoJa8YeGzUG8U4jprCWDtR07R7kwvUk9hqCwcNw==";

    private final InfluxDBClient client;
    private final QueryApi queryApi;

    InfluxDBQueryCenter() {
        client = InfluxDBClientFactory.create(INFLUXDB_URL, INFLUXDB_TOKEN.toCharArray(), INFLUXDB_ORG);
        queryApi = client.getQueryApi();
    }

    public List<FluxTable> requestFluxTable(String flux) {
        return queryApi.query(flux);
    }
}
