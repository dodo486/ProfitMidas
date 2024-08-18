package bulls.feed.abstraction;

import bulls.packet.OffsetLength;

public interface FeedParser {
    OffsetLength parser();
}
