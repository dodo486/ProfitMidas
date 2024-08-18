package bulls.feed.current.parser.kospiFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Future_종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(19, 2, PacketType.String),
    priceSign(21, 1, PacketType.String),
    price(22, 5, PacketType.Integer),

    totalContractAmount(28, 7, PacketType.Integer),
    totalContractPrice(35, 12, PacketType.Integer),

    bidPrice(61, 5, PacketType.Integer),
    bidAmount(66, 6, PacketType.Integer),
    askPrice(127, 5, PacketType.Integer),
    askAmount(132, 6, PacketType.Integer);


    Future_종가(int offSet, int length, PacketType type) {
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
