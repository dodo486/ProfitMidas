package bulls.dmaLog.enums;

import java.util.Set;

public enum DMALogMarketType {
    EQUITY(Set.of("ls")),
    DERIVATIVES(Set.of("lf")),
    ALL(Set.of("ls", "lf"));

    public final Set<String> keywordSet;

    DMALogMarketType(Set<String> keywordSet) {
        this.keywordSet = keywordSet;
    }

    public boolean checkMarket(String marketStr) {
        return keywordSet.contains(marketStr);
    }
}
