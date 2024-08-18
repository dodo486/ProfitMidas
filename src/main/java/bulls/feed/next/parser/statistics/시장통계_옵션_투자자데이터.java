package bulls.feed.next.parser.statistics;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 시장통계_옵션_투자자데이터 implements FeedParser {
    trCode(0, 5, PacketType.String), // P0 / 01:주식 /1:유가증권 2:코스닥  데이터구분값(0, 2, PacketType.String), +  정보구분값(2, 3, PacketType.String),
    생성일자(5, 8, PacketType.String),  //생성일자(5, 8, PacketType.String),
    생성시각(13, 6, PacketType.String), //통계산출시각(13, 6, PacketType.String),
//  못찾음
//    데이터구분(0, 2, PacketType.String), // "*전일확정 : 00, 당일잠정 : 01, 당일확정 : 02, E1 : 장중 데이터 완료, E2 : 확정 데이터 완료    최종 record '99' set"
    상품ID(21, 11, PacketType.String),  //상품ID(21, 11, PacketType.String),
    옵션구분(32, 1, PacketType.String),  //선물옵션구분코드(32, 1, PacketType.String),
    투자자유형(33, 4, PacketType.String), //투자자구분코드(33, 4, PacketType.String),
    매수약정수량(37, 10, PacketType.Integer), //매수거래량(37, 10, PacketType.Integer),
    매도약정수량(47, 10, PacketType.Integer),  //매도거래량(47, 10, PacketType.Integer),
    매수약정대금(57, 22, PacketType.Float),  //매수거래대금(57, 22, PacketType.Float),
    매도약정대금(79, 22, PacketType.Float);  //매도거래대금(79, 22, PacketType.Float),

    시장통계_옵션_투자자데이터(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
