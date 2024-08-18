package bulls.feed.abstraction;

import bulls.feed.current.enums.FeedTRCode;

public interface FeedLauncher {
    void startListen(FeedTRCode[] trCodeList);
}
