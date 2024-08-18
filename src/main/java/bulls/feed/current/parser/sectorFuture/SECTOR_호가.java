package bulls.feed.current.parser.sectorFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SECTOR_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(23, 2, PacketType.String),

    bidSign(36, 1, PacketType.String),
    bidPrice(37, 8, PacketType.Integer),
    bidAmount(45, 8, PacketType.Integer),
    askSign(130, 1, PacketType.String),
    askPrice(131, 8, PacketType.Integer),
    askAmount(139, 8, PacketType.Integer),
    totalBidAmount(27, 9, PacketType.Integer),
    totalAskAmount(121, 9, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPriceSign(309, 1, PacketType.String),
    expectedPrice(310, 8, PacketType.Integer);

    SECTOR_호가(int offSet, int length, PacketType type) {
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
