package bulls.feed.current.parser.stockFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SF_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(21, 2, PacketType.String),

    bidSign(33, 1, PacketType.String),
    bidPrice(34, 7, PacketType.Integer),
    bidAmount(41, 7, PacketType.Integer),
    askSign(191, 1, PacketType.String),
    askPrice(192, 7, PacketType.Integer),
    askAmount(199, 7, PacketType.Integer),
    totalBidAmount(25, 8, PacketType.Integer),
    totalAskAmount(183, 8, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPriceSign(439, 1, PacketType.String),
    expectedPrice(440, 7, PacketType.Integer);

    SF_호가(int offSet, int length, PacketType type) {
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
