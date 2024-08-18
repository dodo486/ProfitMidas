package bulls.feed.current.parser.statistics;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 시장통계_옵션_투자자데이터 implements FeedParser {
    trCode(0, 5, PacketType.String), // P0 / 01:주식 /1:유가증권 2:코스닥
    생성일자(5, 8, PacketType.Integer),
    생성시각(13, 6, PacketType.Integer),
    데이터구분(19, 2, PacketType.String),
    상품ID(21, 11, PacketType.Integer),
    옵션구분(32, 1, PacketType.Integer),
    투자자유형(33, 4, PacketType.String),
    매수약정수량(37, 9, PacketType.Integer),
    매도약정수량(46, 9, PacketType.Integer),
    매수약정대금(55, 18, PacketType.Integer),
    매도약정대금(73, 18, PacketType.Integer);

    시장통계_옵션_투자자데이터(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
