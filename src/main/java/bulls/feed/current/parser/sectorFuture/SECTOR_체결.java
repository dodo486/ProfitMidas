package bulls.feed.current.parser.sectorFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SECTOR_체결 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(23, 2, PacketType.String),

    priceSign(25, 1, PacketType.String),
    price(26, 8, PacketType.Integer),
    amount(34, 10, PacketType.Integer),
    totalAmount(106, 11, PacketType.Integer),
    totalPrice(117, 15, PacketType.Integer),
    buySellSign(143, 1, PacketType.String);

    SECTOR_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

}
