package bulls.dmaLog.enums;

import java.util.Set;

public enum DMALogConnectionType {
    CLIENT(Set.of("tr", "ts")),
    KRX(Set.of("xr", "xs")),
    ALL(Set.of("tr", "ts", "xr", "xs"));

    public final Set<String> keywordSet;

    DMALogConnectionType(Set<String> keywordSet) {
        this.keywordSet = keywordSet;
    }

    public boolean checkConnection(String connectionStr) {
        return keywordSet.contains(connectionStr);
    }
}
