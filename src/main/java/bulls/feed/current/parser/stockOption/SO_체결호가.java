package bulls.feed.current.parser.stockOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SO_체결호가 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(22, 2, PacketType.String),

    price(24, 7, PacketType.Integer),
    amount(31, 7, PacketType.Integer),

    totalAmount(76, 7, PacketType.Integer),
    totalPrice(83, 15, PacketType.Integer),

    buySellSign(98, 1, PacketType.String),
    bidPrice(106, 7, PacketType.Integer),
    bidAmount(113, 7, PacketType.Integer),
    askPrice(253, 7, PacketType.Integer),
    askAmount(260, 7, PacketType.Integer),
    totalBidAmount(99, 7, PacketType.Integer),
    totalAskAmount(246, 7, PacketType.Integer);

    //20180730 변경
//    trCode(0, 5 , PacketType.String ),
//    isinCode(5, 12 , PacketType.String),
//    price(22 ,7 , PacketType.Integer),
//    amount(30 ,7 , PacketType.Integer),
//    bidPrice(105 ,7 , PacketType.Integer),
//    bidAmount(112 , 7 , PacketType.Integer),
//    askPrice(252 ,7 , PacketType.Integer),
//    askAmount(259 , 7 , PacketType.Integer),
//    totalBidAmount(98 ,7 , PacketType.Integer),
//    totalAskAmount(245 ,7 , PacketType.Integer);

    SO_체결호가(int offSet, int length, PacketType type) {
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
