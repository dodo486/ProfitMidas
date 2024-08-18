package bulls.feed.current.parser.kospiOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Option_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(21, 2, PacketType.String),

    bidPrice(32, 5, PacketType.Integer),
    bidAmount(37, 7, PacketType.Integer),
    askPrice(99, 5, PacketType.Integer),
    askAmount(104, 7, PacketType.Integer),
    totalBidAmount(25, 7, PacketType.Integer),
    totalAskAmount(92, 7, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPrice(217, 5, PacketType.Integer);

    Option_호가(int offSet, int length, PacketType type) {
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
