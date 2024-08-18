package bulls.feed.current.parser.stockOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SO_가격제한폭확대 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    보드ID(33, 2, PacketType.String),
    가격확대시각(35, 8, PacketType.String),
    가격제한확대상한단계(43, 2, PacketType.String),
    가격제한확대하한단계(45, 2, PacketType.String),
    상한가(47, 7, PacketType.Integer),
    하한가(54, 7, PacketType.Integer);

    SO_가격제한폭확대(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}