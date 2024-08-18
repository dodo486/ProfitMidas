package bulls.feed.current.parser.kospiFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;
public enum Future_체결 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    //code(7, 9, PacketType.String),
    boardId(19, 2, PacketType.String),

    priceSign(21, 1, PacketType.String),
    price(22, 5, PacketType.Integer),
    amount(27, 6, PacketType.Integer),
    totalAmount(77, 7, PacketType.Integer),
    totalPrice(84, 12, PacketType.Integer),
    buySellSign(103, 1, PacketType.String);

    Future_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
