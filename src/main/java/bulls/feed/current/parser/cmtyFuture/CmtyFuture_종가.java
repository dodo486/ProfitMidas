package bulls.feed.current.parser.cmtyFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum CmtyFuture_종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(20, 2, PacketType.String),
    priceSign(22, 1, PacketType.String),
    price(23, 8, PacketType.Integer),   // 원래 9자리이지만 8자리만 파싱

    totalContractAmount(33, 7, PacketType.Integer),
    totalContractPrice(40, 15, PacketType.Integer),

    bidPriceSign(76, 1, PacketType.String),
    bidPrice(77, 8, PacketType.Integer),    // 원래 9자리이지만 8자리만 파싱
    bidAmount(86, 6, PacketType.Integer),
    askPriceSign(163, 1, PacketType.String),
    askPrice(164, 8, PacketType.Integer),   // 원래 9자리이지만 8자리만 파싱
    askAmount(173, 6, PacketType.Integer);

    CmtyFuture_종가(int offSet, int length, PacketType type) {
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
