package bulls.feed.next.parser.etc;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 공시 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    ////code(7, 9, PacketType.String),
    noticeIndex(17, 6, PacketType.Integer),  //공시연단위일련번호(17, 6, PacketType.Integer),
    noticeTotalPage(23, 5, PacketType.Integer), //공시문서총페이지번호(23, 5, PacketType.Integer),
    noticeCurrPage(28, 5, PacketType.Integer), //공시문서페이지번호(28, 5, PacketType.Integer),
    noticeDate(33, 8, PacketType.Integer), //공시일자(33, 8, PacketType.String),
    marketType(49, 1, PacketType.String), // 공시시장구분코드(49, 1, PacketType.String),	1:유가증권 2:코스닥 3:파생상품 4:채권 5:시장감시 6:코넥스
    productName(50, 40, PacketType.String),  //종목약명(50, 40, PacketType.String),
    title(97, 264, PacketType.String), //공시문서제목(97, 264, PacketType.String),
    content(361, 1000, PacketType.String); //공시문서내용(361, 1000, PacketType.String),


    공시(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
