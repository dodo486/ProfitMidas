package bulls.feed.current.parser.sectorFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SectorF_가격제한폭확대 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    보드ID(34, 2, PacketType.String),
    가격확대시각(36, 8, PacketType.String),
    가격제한확대상한단계(44, 2, PacketType.String),
    가격제한확대하한단계(46, 2, PacketType.String),
    상한가(49, 8, PacketType.Integer),
    하한가(58, 8, PacketType.Integer);

    SectorF_가격제한폭확대(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
