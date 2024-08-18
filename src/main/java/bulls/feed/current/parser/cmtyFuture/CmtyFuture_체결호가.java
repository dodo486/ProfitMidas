package bulls.feed.current.parser.cmtyFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum CmtyFuture_체결호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(20, 2, PacketType.String),

    priceSign(22, 1, PacketType.String),
    price(23, 8, PacketType.Integer),   // 원래 9자리이지만 8자리만 파싱
    amount(32, 6, PacketType.Integer),

    totalAmount(106, 7, PacketType.Integer),
    totalPrice(113, 15, PacketType.Integer),

    buySellSign(142, 1, PacketType.String),
    bidSign(150, 1, PacketType.String),
    bidPrice(151, 8, PacketType.Integer),   // 원래 9자리이지만 8자리만 파싱
    bidAmount(160, 6, PacketType.Integer),
    askSign(237, 1, PacketType.String),
    askPrice(238, 8, PacketType.Integer),   // 원래 9자리이지만 8자리만 파싱
    askAmount(247, 6, PacketType.Integer),
    totalBidAmount(143, 7, PacketType.Integer),
    totalAskAmount(230, 7, PacketType.Integer);


    CmtyFuture_체결호가(int offSet, int length, PacketType type) {
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
