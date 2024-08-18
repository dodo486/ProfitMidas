package bulls.feed.current.parser.kospiOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Option_체결 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(21, 2, PacketType.String),

    price(23, 5, PacketType.Integer),
    amount(28, 7, PacketType.Integer),
    totalAmount(65, 8, PacketType.Integer),
    totalPrice(73, 11, PacketType.Integer),
    buySellSign(92, 1, PacketType.String);

    Option_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
