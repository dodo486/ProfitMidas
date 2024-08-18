package bulls.feed.next.parser.statistics;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 시장통계_거래원 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),

    매도거래원번호(25, 5, PacketType.Integer),
    매도체결수량(30, 12, PacketType.Integer),
    매도거래대금(42, 18, PacketType.Integer),
    매수거래원번호(60, 5, PacketType.Integer),
    매수체결수량(65, 12, PacketType.Integer),
    매수거래대금(77, 18, PacketType.Integer);

    시장통계_거래원(int offSet, int length, PacketType type) {
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
