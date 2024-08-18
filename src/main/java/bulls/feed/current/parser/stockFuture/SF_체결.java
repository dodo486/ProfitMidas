package bulls.feed.current.parser.stockFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SF_체결 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(21, 2, PacketType.String),

    priceSign(23, 1, PacketType.String),
    price(24, 7, PacketType.Integer),
    amount(31, 6, PacketType.Integer),
    totalAmount(93, 7, PacketType.Integer),
    totalPrice(100, 15, PacketType.Integer),
    buySellSign(115, 1, PacketType.String);

    SF_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

}
