package bulls.feed.current.parser.etc;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 지수 implements FeedParser {
    trCode(0, 5, PacketType.String),
    indexSpecification(5, 3, PacketType.String),
    index(14, 8, PacketType.Float),
    indexCode(0, 8, PacketType.String),
    volume(31, 8, PacketType.Integer),
    money(39, 8, PacketType.Integer),
    time(8, 6, PacketType.String); // "junjjj" for last update of each date


    지수(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
