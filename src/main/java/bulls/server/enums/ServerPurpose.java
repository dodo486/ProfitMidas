package bulls.server.enums;

import bulls.channel.WO.WORemainLevel;

public enum ServerPurpose {
    LP(WORemainLevel.NO),
    MAIN_HEDGE(WORemainLevel.ALL),
    SUB_HEDGE(WORemainLevel.SELECTIVE),
    EXPIRY_CLEARING(WORemainLevel.ALL),
    TEST(WORemainLevel.NO),
    UNKNOWN(WORemainLevel.NO);

    private final WORemainLevel woRemainLevel;

    ServerPurpose(WORemainLevel woRemainLevel) {
        this.woRemainLevel = woRemainLevel;
    }

    public WORemainLevel getWORemainLevel() {
        return woRemainLevel;
    }
}
