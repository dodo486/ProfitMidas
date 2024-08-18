package bulls.feed.current.parser.etf;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum ETF_NAV implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCodeEtf(5, 12, PacketType.String),
    codeEtf(8, 6, PacketType.String),

    time(17, 6, PacketType.String),
    NAV(32, 9, PacketType.Float);

    ETF_NAV(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
