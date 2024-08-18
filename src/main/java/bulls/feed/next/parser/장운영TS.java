package bulls.feed.next.parser;

import bulls.annotation.Hint;
import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

@Hint(info = "ELW 포함")
public enum 장운영TS implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    보드Id(22, 2, PacketType.String),
    세션ID(38, 2, PacketType.Integer),
    종목코드(22, 2, PacketType.String),
    정보분배종목인덱스(22, 2, PacketType.String),
    매매처리시각(22, 2, PacketType.String),
    보드이벤트Id(24, 3, PacketType.String),
    보드이벤트시작시간(27, 6, PacketType.Integer),
    보드이벤트적용군코드(33, 5, PacketType.String),
    거래정지사유코드(40, 3, PacketType.Integer),
    정보분배메세지종료키워드(40, 3, PacketType.Integer); //

    장운영TS(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
