package bulls.feed.current.parser.stockOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SO_체결 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(22, 2, PacketType.String),

    price(24, 7, PacketType.Integer),
    amount(31, 7, PacketType.Integer),
    totalAmount(76, 7, PacketType.Integer),
    totalPrice(83, 15, PacketType.Integer),
    buySellSign(98, 1, PacketType.String);

    SO_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

}