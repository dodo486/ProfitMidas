package bulls.feed.current.parser.stockFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SF_체결호가 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(21, 2, PacketType.String),

    priceSign(23, 1, PacketType.String),
    price(24, 7, PacketType.Integer),
    amount(31, 6, PacketType.Integer),

    totalAmount(93, 7, PacketType.Integer),
    totalPrice(100, 15, PacketType.Integer),

    buySellSign(115, 1, PacketType.String), //1:매도,2:매수(최종으로 들어온 호가의 매도매수구분값)
    bidSign(124, 1, PacketType.String),
    bidPrice(125, 7, PacketType.Integer),
    bidAmount(132, 7, PacketType.Integer),
    askSign(282, 1, PacketType.String),
    askPrice(283, 7, PacketType.Integer),
    askAmount(290, 7, PacketType.Integer),
    totalBidAmount(116, 8, PacketType.Integer),
    totalAskAmount(274, 8, PacketType.Integer);

    SF_체결호가(int offSet, int length, PacketType type) {
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
