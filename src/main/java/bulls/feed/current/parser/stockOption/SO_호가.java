package bulls.feed.current.parser.stockOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SO_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(22, 2, PacketType.String),

    bidPrice(33, 7, PacketType.Integer),
    bidAmount(40, 7, PacketType.Integer),
    askPrice(180, 7, PacketType.Integer),
    askAmount(187, 7, PacketType.Integer),
    totalBidAmount(26, 7, PacketType.Integer),
    totalAskAmount(173, 7, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPrice(440, 7, PacketType.Integer);

    SO_호가(int offSet, int length, PacketType type) {
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
