package bulls.feed.current.parser.kospiFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum K200선물_가격제한폭확대 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    보드ID(30, 2, PacketType.String),
    가격확대시각(32, 8, PacketType.String),
    가격제한확대상한단계(40, 2, PacketType.String),
    가격제한확대하한단계(42, 2, PacketType.String),
    상한가(45, 5, PacketType.Integer),
    하한가(51, 5, PacketType.Integer);

    K200선물_가격제한폭확대(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
