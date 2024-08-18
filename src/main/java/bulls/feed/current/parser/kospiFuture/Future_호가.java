package bulls.feed.current.parser.kospiFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Future_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(19, 2, PacketType.String),

    bidSign(29, 1, PacketType.String),
    bidPrice(30, 5, PacketType.Integer),
    bidAmount(35, 6, PacketType.Integer),
    askSign(95, 1, PacketType.String),
    askPrice(96, 5, PacketType.Integer),
    askAmount(101, 6, PacketType.Integer),
    totalBidAmount(23, 6, PacketType.Integer),
    totalAskAmount(89, 6, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPriceSign(213, 1, PacketType.String),
    expectedPrice(214, 5, PacketType.Integer);

    Future_호가(int offSet, int length, PacketType type) {
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
