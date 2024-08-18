package bulls.feed.next.parser.statistics;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

// 종목별 투자자별 종가통계
public enum 종목별_투자자별_종가통계 implements FeedParser {
    trCode(0, 5, PacketType.String),  //데이터구분값(0, 2, PacketType.String) + 정보구분값(2, 3, PacketType.String),
    DATA구분(0, 2, PacketType.String),//C1    데이터구분값(0, 2, PacketType.String)
    정보구분(2, 2, PacketType.String),//01:주식    정보구분값(2, 2, PacketType.String), *(구) 정보구분값+시장구분값
    시장구분(4, 1, PacketType.String),//9:금현물   정보구분값(4, 1, PacketType.String), *(구) 정보구분값+시장구분값
    isinCode(5, 12, PacketType.String),  //종목코드(5, 12, PacketType.String),
    종목코드(5, 12, PacketType.String),//표준코드   종목코드(5, 12, PacketType.String),
    일련번호(17, 6, PacketType.Integer),//1~99999999 종목일련번호      정보분배종목인덱스(17, 6, PacketType.Integer),
    투자자코드(23, 4, PacketType.String),//※코드값모음 참조  투자자구분코드(23, 4, PacketType.String),
    매도체결수량(27, 12, PacketType.Integer),//단위:주   누적매도체결수량(27, 12, PacketType.Integer),
    매도거래대금(39, 22, PacketType.Integer),//단위:원   누적매도거래대금(39, 22, PacketType.Float),
    매수체결수량(61, 12, PacketType.Integer),//단위:주   누적매수체결수량(61, 12, PacketType.Integer),
    매수거래대금(73, 22, PacketType.Integer),//단위:원   누적매수거래대금(73, 22, PacketType.Float),
    // 못찾음 (feed에서 사용되지 않는 정보 )
    // FILLER(793, 6, PacketType.String),
    FF(95, 1, PacketType.String);//END OF TEXT (%HFF)  정보분배메세지종료키워드(95, 1, PacketType.String),

    종목별_투자자별_종가통계(int offSet, int length, PacketType type) {
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
