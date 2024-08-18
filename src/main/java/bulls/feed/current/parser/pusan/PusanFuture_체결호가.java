package bulls.feed.current.parser.pusan;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum PusanFuture_체결호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),

    priceSign(27, 1, PacketType.String),
    price(28, 5, PacketType.Integer),
    amount(33, 6, PacketType.Integer),

    totalAmount(87, 7, PacketType.Integer),
    totalPrice(94, 12, PacketType.Integer),

    buySellSign(106, 1, PacketType.String),
    bidSign(113, 1, PacketType.String),
    bidPrice(114, 5, PacketType.Integer),
    bidAmount(119, 6, PacketType.Integer),
    askSign(179, 1, PacketType.String),
    askPrice(180, 5, PacketType.Integer),
    askAmount(185, 6, PacketType.Integer),
    totalBidAmount(107, 6, PacketType.Integer),
    totalAskAmount(173, 6, PacketType.Integer);


    PusanFuture_체결호가(int offSet, int length, PacketType type) {
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
