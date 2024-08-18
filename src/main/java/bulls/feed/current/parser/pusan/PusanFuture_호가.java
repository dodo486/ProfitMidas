package bulls.feed.current.parser.pusan;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum PusanFuture_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    bidSign(35, 1, PacketType.String),
    bidPrice(36, 5, PacketType.Integer),
    bidAmount(41, 6, PacketType.Integer),
    askSign(101, 1, PacketType.String),
    askPrice(102, 5, PacketType.Integer),
    askAmount(107, 6, PacketType.Integer),
    totalBidAmount(29, 6, PacketType.Integer),
    totalAskAmount(95, 6, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPriceSign(223, 1, PacketType.String),
    expectedPrice(224, 5, PacketType.Integer);

    PusanFuture_호가(int offSet, int length, PacketType type) {
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
