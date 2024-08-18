package bulls.feed.current.parser.kospiOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Option_Recovery implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    bidPrice(94, 5, PacketType.Integer),
    bidAmount(99, 7, PacketType.Integer),
    bidOrderCount(106, 4, PacketType.Integer),
    askPrice(78, 5, PacketType.Integer),
    askAmount(83, 7, PacketType.Integer),
    askOrderCount(90, 4, PacketType.Integer);


    Option_Recovery(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

    public OffsetLength plus(int plus) {
        return new OffsetLength(m.getOffset() + plus, m.getLength(), m.getType());
    }
}