package bulls.feed.current.parser.pusan;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum PusanOption_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    bidPrice(36, 5, PacketType.Integer),
    bidAmount(41, 7, PacketType.Integer),
    askPrice(103, 5, PacketType.Integer),
    askAmount(108, 7, PacketType.Integer),
    totalBidAmount(29, 7, PacketType.Integer),
    totalAskAmount(96, 7, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPrice(225, 5, PacketType.Integer);

    PusanOption_호가(int offSet, int length, PacketType type) {
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