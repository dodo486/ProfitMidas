package bulls.feed.current.parser.kosdaqOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum KQOption_체결호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(21, 2, PacketType.String),

    price(23, 6, PacketType.Integer),
    amount(29, 6, PacketType.Integer),

    totalAmount(69, 7, PacketType.Integer),
    totalPrice(76, 11, PacketType.Integer),

    bidPrice(101, 6, PacketType.Integer),
    bidAmount(107, 6, PacketType.Integer),
    askPrice(167, 6, PacketType.Integer),
    askAmount(173, 6, PacketType.Integer),
    buySellSign(94, 1, PacketType.Integer),
    totalBidAmount(95, 6, PacketType.Integer),
    totalAskAmount(161, 6, PacketType.Integer);


    KQOption_체결호가(int offSet, int length, PacketType type) {
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
