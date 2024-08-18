package bulls.feed.current.parser.kosdaqFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum KQ_체결호가 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(19, 2, PacketType.String),

    buySellSign(109, 1, PacketType.String),
    price(22, 6, PacketType.Integer),
    amount(28, 6, PacketType.Integer),

    totalAmount(84, 7, PacketType.Integer),
    totalPrice(91, 11, PacketType.Integer),

    bidSign(116, 1, PacketType.String),
    bidPrice(117, 6, PacketType.Integer),
    bidAmount(123, 6, PacketType.Integer),
    askSign(187, 1, PacketType.String),
    askPrice(188, 6, PacketType.Integer),
    askAmount(194, 6, PacketType.Integer),
    totalBidAmount(110, 6, PacketType.Integer),
    totalAskAmount(181, 6, PacketType.Integer);

    KQ_체결호가(int offSet, int length, PacketType type) {
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
