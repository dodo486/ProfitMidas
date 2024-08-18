package bulls.feed.current.parser.etc;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum NeedToImplement implements FeedParser {
    Instance;

    private static final OffsetLength emptyOffsetLength = new OffsetLength(0, 0, PacketType.String);

    @Override
    public OffsetLength parser() {
        return emptyOffsetLength;
    }
}
