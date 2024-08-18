package bulls.feed.current.parser.euroStoxx50;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum EuroStoxx50_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(19, 2, PacketType.String),

    bidSign(30, 1, PacketType.String),
    bidPrice(31, 6, PacketType.Integer),
    bidAmount(37, 6, PacketType.Integer),
    askSign(102, 1, PacketType.String),
    askPrice(103, 6, PacketType.Integer),
    askAmount(109, 6, PacketType.Integer),
    totalBidAmount(23, 7, PacketType.Integer),
    totalAskAmount(95, 7, PacketType.Integer),

    // 예상체결가는 단일가매매시에만 제공
    expectedPriceSign(225, 1, PacketType.String),
    expectedPrice(226, 6, PacketType.Integer);

    EuroStoxx50_호가(int offSet, int length, PacketType type) {
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
