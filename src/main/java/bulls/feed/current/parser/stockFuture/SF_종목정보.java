package bulls.feed.current.parser.stockFuture;

import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SF_종목정보 {
    trCode(0, 5, PacketType.String),
    isinCode(18, 12, PacketType.String),

    spreadRecentCode(414, 12, PacketType.String),
    spreadNextCode(426, 12, PacketType.String),
    multiplier(500 + 3, 21, PacketType.Float),    // .xx 식의 소숫점 두자리 식으로만 파싱하게 되어있으므로 일단 블락..
    underlyingIsinCode(537 + 3, 12, PacketType.String),
    underlyingCode(541 + 3, 6, PacketType.String),
    underlyingClosingPrice(549 + 3, 12, PacketType.Float);

    SF_종목정보(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
