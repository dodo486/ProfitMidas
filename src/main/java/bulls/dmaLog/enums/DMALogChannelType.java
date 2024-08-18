package bulls.dmaLog.enums;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public enum DMALogChannelType {
    MASTER_CHANNEL,
    CLIENT_CHANNEL,
    ALL_CHANNEL;

    private final Set<String> processTypeSet = Set.of("sm", "fm");

    // 클라이언트 통신하는 프로세스 : 현물 sm, 파생 fm
    public boolean checkChannel(String channelStr) {
        if (channelStr.length() != 5)
            return false;

        String processTypeStr = channelStr.substring(0, 2);
        byte[] channelStrBytes = channelStr.getBytes(StandardCharsets.UTF_8);

        int num = 0;
        for (int i = 0; i < 3; i++) {
            num *= 10;
            byte b = channelStrBytes[i + 2];
            if (b < '0' || b > '9')
                return false;

            num += b - '0';
        }

        return processTypeSet.contains(processTypeStr) && checkChannelNumber(num);
    }

    // Master channel은 5xx
    private boolean checkChannelNumber(int channelNumber) {
        if (this == ALL_CHANNEL)
            return true;

        if (this == MASTER_CHANNEL)
            return 500 <= channelNumber && channelNumber < 600;

        return channelNumber < 500;
    }
}
