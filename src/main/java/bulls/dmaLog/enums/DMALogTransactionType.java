package bulls.dmaLog.enums;

import bulls.dmaLog.DMALog;
import bulls.dmaLog.ReportDMALog;
import bulls.dmaLog.RequestDMALog;
import bulls.dmaLog.TradeDMALog;

public enum DMALogTransactionType {
    SINGLE_ORDER('S', RequestDMALog.class),
    CONFIRMED('C', ReportDMALog.class),
    REJECTED('R', ReportDMALog.class),
    EXPIRED('A', ReportDMALog.class),   // Auto Cancel
    CONVERTED_IMMEDIATELY('I', ReportDMALog.class),
    CONVERTED_POST('P', ReportDMALog.class),
    WARNING('W', ReportDMALog.class),
    FILLED('F', TradeDMALog.class),
    UNKNOWN(' ', null);

    public final char code;
    public final Class<? extends DMALog> clazz;

    DMALogTransactionType(char code, Class<? extends DMALog> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public static DMALogTransactionType of(char code) {
        for (var state : values()) {
            if (state.code == code)
                return state;
        }

        return UNKNOWN;
    }
}
