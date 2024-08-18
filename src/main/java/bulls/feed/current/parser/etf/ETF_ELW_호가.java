package bulls.feed.current.parser.etf;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

/**
 * ETF/ELW 호가는 B6011로는 들어오지 아니함.
 */
public enum ETF_ELW_호가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(768, 2, PacketType.String),
    volume(22, 12, PacketType.Integer), //누적체결수량
    askPrice(34, 9, PacketType.Integer),
    bidPrice(43, 9, PacketType.Integer),
    askAmount(52, 12, PacketType.Integer),
    bidAmount(64, 12, PacketType.Integer),
    lpAskAmount(76, 12, PacketType.Integer),
    lpBidAmount(88, 12, PacketType.Integer),
    totalBidAmount(694, 12, PacketType.Integer),
    totalAskAmount(706, 12, PacketType.Integer),
    expPrice(770, 9, PacketType.Integer),
    expAmount(779, 12, PacketType.Integer);

    ETF_ELW_호가(int offSet, int length, PacketType type) {
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
