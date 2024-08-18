package bulls.feed.current.parser.kospiOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Option_체결호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(21, 2, PacketType.String),

    price(23, 5, PacketType.Integer),
    amount(28, 7, PacketType.Integer),

    totalAmount(65, 8, PacketType.Integer),
    totalPrice(73, 11, PacketType.Integer),

    buySellSign(92, 1, PacketType.String),
    bidPrice(100, 5, PacketType.Integer),
    bidAmount(105, 7, PacketType.Integer),
    askPrice(167, 5, PacketType.Integer),
    askAmount(172, 7, PacketType.Integer),
    totalBidAmount(93, 7, PacketType.Integer),
    totalAskAmount(160, 7, PacketType.Integer);

    Option_체결호가(int offSet, int length, PacketType type) {
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
