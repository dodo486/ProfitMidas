package bulls.feed.current.parser.pusan;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum PusanCmtyFuture_실시간상하한가 implements FeedParser {
    trCode(0, 5,PacketType.String),
    isinCode(5, 12, PacketType.String),
    보드ID(25, 2, PacketType.String),
    처리시각(27, 12, PacketType.String),
    실시간가격제한설정코드(39, 1, PacketType.String),
    상한가(41, 8, PacketType.Integer),
    하한가(50, 8, PacketType.Integer);

    PusanCmtyFuture_실시간상하한가(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
