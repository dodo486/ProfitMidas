package bulls.feed.current.parser.cmtyFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum CmtyFuture_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(20, 2, PacketType.String),

    bidSign(31, 1, PacketType.String),
    bidPrice(32, 8, PacketType.Integer),    // 원래 9자리이지만 8자리만 파싱
    bidAmount(41, 6, PacketType.Integer),
    askSign(118, 1, PacketType.String),
    askPrice(119, 8, PacketType.Integer),   // 원래 9자리이지만 8자리만 파싱
    askAmount(128, 6, PacketType.Integer),
    totalBidAmount(24, 7, PacketType.Integer),
    totalAskAmount(111, 7, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPriceSign(256, 1, PacketType.String),
    expectedPrice(257, 8, PacketType.Integer);  // 원래 9자리이지만 8자리만 파싱

    CmtyFuture_호가(int offSet, int length, PacketType type) {
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
