package bulls.feed.current.parser.pusan;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum PusanFuture_체결 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    priceSign(27, 1, PacketType.String),
    price(28, 5, PacketType.Integer),
    amount(33, 6, PacketType.Integer),
    totalAmount(87, 7, PacketType.Integer),
    totalPrice(94, 12, PacketType.Integer),
    buySellSign(106, 1, PacketType.String);

    PusanFuture_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
