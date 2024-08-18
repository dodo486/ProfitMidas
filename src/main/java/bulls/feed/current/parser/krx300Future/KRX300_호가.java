package bulls.feed.current.parser.krx300Future;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum KRX300_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(19, 2, PacketType.String),

    bidSign(29, 1, PacketType.String),
    bidPrice(30, 6, PacketType.Integer),
    bidAmount(36, 6, PacketType.Integer),
    askSign(100, 1, PacketType.String),
    askPrice(101, 6, PacketType.Integer),
    askAmount(107, 6, PacketType.Integer),
    totalBidAmount(23, 6, PacketType.Integer),
    totalAskAmount(94, 6, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPriceSign(223, 1, PacketType.String),
    expectedPrice(224, 6, PacketType.Integer);

    KRX300_호가(int offSet, int length, PacketType type) {
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
