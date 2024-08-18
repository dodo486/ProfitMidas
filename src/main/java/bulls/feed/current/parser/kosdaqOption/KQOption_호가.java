package bulls.feed.current.parser.kosdaqOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum KQOption_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(21, 2, PacketType.String),

    bidPrice(31, 6, PacketType.Integer),
    bidAmount(37, 6, PacketType.Integer),
    askPrice(97, 6, PacketType.Integer),
    askAmount(103, 6, PacketType.Integer),
    totalBidAmount(25, 6, PacketType.Integer),
    totalAskAmount(91, 6, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPrice(215, 6, PacketType.Integer);

    KQOption_호가(int offSet, int length, PacketType type) {
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
