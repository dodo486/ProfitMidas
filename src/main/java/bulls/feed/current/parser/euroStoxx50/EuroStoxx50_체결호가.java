package bulls.feed.current.parser.euroStoxx50;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum EuroStoxx50_체결호가 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(19, 2, PacketType.String),

    priceSign(21, 1, PacketType.String),
    price(22, 6, PacketType.Integer),
    amount(28, 6, PacketType.Integer),

    totalAmount(84, 7, PacketType.Integer),
    totalPrice(91, 11, PacketType.Integer),

    buySellSign(109, 1, PacketType.String),
    bidSign(117, 1, PacketType.String),
    bidPrice(118, 6, PacketType.Integer),
    bidAmount(124, 6, PacketType.Integer),
    askSign(189, 1, PacketType.String),
    askPrice(190, 6, PacketType.Integer),
    askAmount(196, 6, PacketType.Integer),
    totalBidAmount(110, 7, PacketType.Integer),
    totalAskAmount(182, 7, PacketType.Integer);

    EuroStoxx50_체결호가(int offSet, int length, PacketType type) {
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
