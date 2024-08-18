package bulls.feed.current.parser.kosdaqFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum KQ_체결 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(19, 2, PacketType.String),

    priceSign(21, 1, PacketType.String),
    price(22, 6, PacketType.Integer),
    amount(28, 6, PacketType.Integer),
    totalAmount(84, 7, PacketType.Integer),
    totalPrice(91, 11, PacketType.Integer),
    buySellSign(109, 1, PacketType.String);

    KQ_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

}
