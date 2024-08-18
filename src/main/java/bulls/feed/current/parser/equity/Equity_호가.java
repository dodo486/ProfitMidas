package bulls.feed.current.parser.equity;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

/**
 * 주식, ELW, ETF 포함
 */

public enum Equity_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(528, 2, PacketType.String),
    volume(22, 12, PacketType.Integer), //누적체결수량
    askPrice(34, 9, PacketType.Integer),
    bidPrice(43, 9, PacketType.Integer),
    askAmount(52, 12, PacketType.Integer),
    bidAmount(64, 12, PacketType.Integer),
    totalBidAmount(430, 12, PacketType.Integer),
    totalAskAmount(442, 12, PacketType.Integer),
    expPrice(530, 9, PacketType.Integer),
    expAmount(539, 12, PacketType.Integer);

    Equity_호가(int offSet, int length, PacketType type) {
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
