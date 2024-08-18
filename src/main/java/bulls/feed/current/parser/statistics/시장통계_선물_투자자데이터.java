package bulls.feed.current.parser.statistics;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 시장통계_선물_투자자데이터 implements FeedParser {
    trCode(0, 5, PacketType.String), // P0 / 01:주식 /1:유가증권 2:코스닥
    생성일자(5, 8, PacketType.Integer),
    생성시각(13, 6, PacketType.Integer),
    데이터구분(19, 2, PacketType.String), //"*전일확정 : 00, 당일잠정 : 01, 당일확정 : 02, E1 : 장중 데이터 완료, E2 : 확정 데이터 완료    최종 record '99' set"

    상품ID(21, 11, PacketType.Integer),
    투자자유형(32, 4, PacketType.String),
    매수약정수량(36, 9, PacketType.Integer),
    매도약정수량(45, 9, PacketType.Integer),
    매수약정대금(54, 18, PacketType.Integer),
    매도약정대금(72, 18, PacketType.Integer),
    매수약정수량_SP(90, 9, PacketType.Integer),
    매도약정수량_SP(99, 9, PacketType.Integer),
    매수약정대금_SP(108, 18, PacketType.Integer),
    매도약정대금_SP(126, 18, PacketType.Integer);

    시장통계_선물_투자자데이터(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
