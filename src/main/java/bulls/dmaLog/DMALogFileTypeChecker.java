package bulls.dmaLog;

import bulls.dmaLog.enums.DMALogChannelType;
import bulls.dmaLog.enums.DMALogConnectionType;
import bulls.dmaLog.enums.DMALogMarketType;

public class DMALogFileTypeChecker {
    // ls_tr_sm001
    // ls : market
    // tr : connection
    // 001 : channel

    private final DMALogMarketType market;
    private final DMALogConnectionType connection;
    private final DMALogChannelType channel;

    public DMALogFileTypeChecker(DMALogMarketType market, DMALogConnectionType connection, DMALogChannelType channel) {
        this.market = market;
        this.connection = connection;
        this.channel = channel;
    }

    public static DMALogFileTypeChecker getDefault() {
        return new DMALogFileTypeChecker(DMALogMarketType.ALL, DMALogConnectionType.CLIENT, DMALogChannelType.CLIENT_CHANNEL);
    }

    public boolean check(String fileName) {
        if (fileName.length() != 11)
            return false;

        String[] split = fileName.split("_");
        if (split.length != 3)
            return false;

        return market.checkMarket(split[0]) && connection.checkConnection(split[1]) && channel.checkChannel(split[2]);
    }
}
