package bulls.feed.current.parser.krx300Future;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum KRX300Futures_종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(19, 2, PacketType.String),
    priceSign(21, 1, PacketType.String),
    price(22, 6, PacketType.Integer),

    totalContractAmount(29, 7, PacketType.Integer),
    totalContractPrice(36, 11, PacketType.Integer),

    bidPrice(61, 6, PacketType.Integer),
    bidAmount(67, 6, PacketType.Integer),
    askPrice(132, 6, PacketType.Integer),
    askAmount(138, 6, PacketType.Integer);


    KRX300Futures_종가(int offSet, int length, PacketType type) {
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
