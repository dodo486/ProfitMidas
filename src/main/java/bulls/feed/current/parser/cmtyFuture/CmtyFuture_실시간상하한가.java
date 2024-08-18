package bulls.feed.current.parser.cmtyFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum CmtyFuture_실시간상하한가 implements FeedParser {
    trCode(0, 5,PacketType.String),
    isinCode(5, 12, PacketType.String),
    보드ID(20, 2, PacketType.String),
    처리시각(22, 8, PacketType.String),
    실시간가격제한설정코드(30, 1, PacketType.String),
    상한가(32, 9, PacketType.Integer),
    하한가(42, 9, PacketType.Integer);

    CmtyFuture_실시간상하한가(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
