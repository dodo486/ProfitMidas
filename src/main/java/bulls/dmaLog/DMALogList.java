package bulls.dmaLog;

import java.util.List;

public class DMALogList {

    private List<DMALog> logList;

    public DMALogList(List<DMALog> logList) {
        this.logList = logList;
    }

    public List<DMALog> getLogList() {
        return logList;
    }

    public void setLogList(List<DMALog> logList) {
        this.logList = logList;
    }
}
