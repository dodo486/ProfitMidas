package bulls.feed.current.parser.pusan;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum PusanOption_체결 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    price(27, 5, PacketType.Integer),
    amount(32, 7, PacketType.Integer),
    totalAmount(73, 8, PacketType.Integer),
    totalPrice(81, 11, PacketType.Integer),
    buySellSign(92, 1, PacketType.String);

    PusanOption_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
