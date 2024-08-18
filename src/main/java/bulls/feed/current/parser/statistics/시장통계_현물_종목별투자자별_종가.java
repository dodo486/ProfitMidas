package bulls.feed.current.parser.statistics;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 시장통계_현물_종목별투자자별_종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    DATA구분(0, 2, PacketType.String),//C1
    정보구분(2, 2, PacketType.String),//01:주식
    시장구분(4, 1, PacketType.String),//9:금현물
    종목코드(5, 12, PacketType.String),//표준코드
    일련번호(17, 8, PacketType.Integer),//1~99999999 종목일련번호
    투자자코드(25, 4, PacketType.String),//※코드값모음 참조
    매도체결수량(29, 12, PacketType.Integer),//단위:주
    매도거래대금(41, 18, PacketType.Integer),//단위:원
    매수체결수량(59, 12, PacketType.Integer),//단위:주
    매수거래대금(71, 18, PacketType.Integer),//단위:원
    FILLER(793, 6, PacketType.String),//
    FF(799, 1, PacketType.String);//END OF TEXT (%HFF)

    시장통계_현물_종목별투자자별_종가(int offSet, int length, PacketType type) {
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
