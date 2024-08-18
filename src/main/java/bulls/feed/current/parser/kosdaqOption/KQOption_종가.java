package bulls.feed.current.parser.kosdaqOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum KQOption_종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),

    price(23, 6, PacketType.Integer),
    totalContractAmount(30, 7, PacketType.Integer),
    totalContractPrice(37, 11, PacketType.Integer),
    bidPrice(61, 6, PacketType.Integer),
    bidAmount(67, 6, PacketType.Integer),
    askPrice(127, 6, PacketType.Integer),
    askAmount(133, 6, PacketType.Integer);


    KQOption_종가(int offSet, int length, PacketType type) {
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
