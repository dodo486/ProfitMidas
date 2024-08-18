package bulls.feed.current.parser.kospiOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Option_종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    price(23, 5, PacketType.Integer),
    totalContractAmount(29, 8, PacketType.Integer),
    totalContractPrice(37, 11, PacketType.Integer),
    bidPrice(63, 5, PacketType.Integer),
    bidAmount(68, 7, PacketType.Integer),
    askPrice(130, 5, PacketType.Integer),
    askAmount(135, 7, PacketType.Integer);

    Option_종가(int offSet, int length, PacketType type) {
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
