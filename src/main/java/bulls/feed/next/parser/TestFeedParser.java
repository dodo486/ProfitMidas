package bulls.feed.next.parser;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum TestFeedParser implements FeedParser {
    TEST_DATA(0, 1, PacketType.String);

    TestFeedParser(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    @Override
    public OffsetLength parser() {
        return m;
    }
}
