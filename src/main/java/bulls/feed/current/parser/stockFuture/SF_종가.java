package bulls.feed.current.parser.stockFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SF_종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),

    boardId(21, 2, PacketType.String),
    priceSign(23, 1, PacketType.String),
    price(24, 7, PacketType.Integer),

    totalContractAmount(32, 7, PacketType.Integer),
    totalContractPrice(39, 15, PacketType.Integer),

    bidPriceSign(62, 1, PacketType.String),
    bidPrice(63, 7, PacketType.Integer),
    bidAmount(70, 7, PacketType.Integer),
    askPriceSign(220, 1, PacketType.String),
    askPrice(221, 7, PacketType.Integer),
    askAmount(228, 7, PacketType.Integer);


    SF_종가(int offSet, int length, PacketType type) {
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
