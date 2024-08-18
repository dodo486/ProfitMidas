package bulls.feed.current.parser.kospiFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Future_체결호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(19, 2, PacketType.String),

    priceSign(21, 1, PacketType.String),
    price(22, 5, PacketType.Integer),
    amount(27, 6, PacketType.Integer),

    totalAmount(77, 7, PacketType.Integer),
    totalPrice(84, 12, PacketType.Integer),

    buySellSign(103, 1, PacketType.String),
    bidSign(110, 1, PacketType.String),
    bidPrice(111, 5, PacketType.Integer),
    bidAmount(116, 6, PacketType.Integer),
    askSign(176, 1, PacketType.String),
    askPrice(177, 5, PacketType.Integer),
    askAmount(182, 6, PacketType.Integer),
    totalBidAmount(104, 6, PacketType.Integer),
    totalAskAmount(170, 6, PacketType.Integer);


    Future_체결호가(int offSet, int length, PacketType type) {
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
