package bulls.feed.next.parser.etc;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 지수 implements FeedParser {
    trCode(0, 5, PacketType.String), // 데이터구분값(0, 2, PacketType.String), 정보구분값(2, 3, PacketType.String),
    // 못찾음
    // indexSpecification(5, 3, PacketType.String),
    indexCode(0, 8, PacketType.String), //  지수ID(5, 6, PacketType.String),
    index(14, 8, PacketType.Integer),  // 지수(29, 9, PacketType.Integer),
    volume(31, 8, PacketType.Integer),  // 누적거래량(48, 12, PacketType.Integer),
    money(39, 8, PacketType.Integer), // 누적거래대금(60, 12, PacketType.Integer),
    time(8, 6, PacketType.String); // "junjjj" for last update of each date     지수산출시각(23, 6, PacketType.String),



    지수(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
