package bulls.feed.current.parser.sectorFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SECTOR_체결호가 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(23, 2, PacketType.String),

    priceSign(25, 1, PacketType.String),
    price(26, 8, PacketType.Integer),
    amount(34, 10, PacketType.Integer),

    totalAmount(106, 11, PacketType.Integer),
    totalPrice(117, 15, PacketType.Integer),

    buySellSign(143, 1, PacketType.String),
    bidSign(153, 1, PacketType.String),
    bidPrice(154, 8, PacketType.Integer),
    bidAmount(162, 8, PacketType.Integer),
    askSign(247, 1, PacketType.String),
    askPrice(248, 8, PacketType.Integer),
    askAmount(256, 8, PacketType.Integer),
    totalBidAmount(144, 9, PacketType.Integer),
    totalAskAmount(238, 9, PacketType.Integer);

    SECTOR_체결호가(int offSet, int length, PacketType type) {
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
