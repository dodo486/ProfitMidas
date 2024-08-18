package bulls.dmaLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class FEPDailyContractLogData {
    private final LocalDate date;
    private final String fileName;

    private final Map<String, List<DMALog>> fullDMALogMap;
    private final Map<String, List<TradeDMALog>> tradeDMALogMap;
    private final Map<String, List<ReportDMALog>> reportDMALogMap;
    private final Map<String, List<RequestDMALog>> requestDMALogMap;

    public FEPDailyContractLogData() {
        date = null;
        fileName = null;
        fullDMALogMap = null;
        tradeDMALogMap = null;
        reportDMALogMap = null;
        requestDMALogMap = null;
    }

    public FEPDailyContractLogData(LocalDate date, String fileName, Map<String, List<DMALog>> fullDMALogMap, Map<String, List<TradeDMALog>> tradeDMALogMap, Map<String, List<ReportDMALog>> reportDMALogMap, Map<String, List<RequestDMALog>> requestDMALogMap) {
        this.date = date;
        this.fileName = fileName;
        this.fullDMALogMap = fullDMALogMap;
        this.tradeDMALogMap = tradeDMALogMap;
        this.reportDMALogMap = reportDMALogMap;
        this.requestDMALogMap = requestDMALogMap;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<String, List<DMALog>> getFullDMALogMap() {
        return fullDMALogMap;
    }

    public Map<String, List<TradeDMALog>> getTradeDMALogMap() {
        return tradeDMALogMap;
    }

    public Map<String, List<ReportDMALog>> getReportDMALogMap() {
        return reportDMALogMap;
    }

    public Map<String, List<RequestDMALog>> getRequestDMALogMap() {
        return requestDMALogMap;
    }
}
