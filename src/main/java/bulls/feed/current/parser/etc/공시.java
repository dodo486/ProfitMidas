package bulls.feed.current.parser.etc;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 공시 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    ////code(7, 9, PacketType.String),
    noticeIndex(17, 6, PacketType.Integer),
    noticeTotalPage(23, 5, PacketType.Integer),
    noticeCurrPage(28, 5, PacketType.Integer),
    noticeDate(33, 8, PacketType.Integer),
    marketType(49, 1, PacketType.String), //1:유가증권 2:코스닥 3:파생상품 4:채권 5:시장감시 6:코넥스
    productName(50, 40, PacketType.String),
    title(97, 264, PacketType.String),
    content(361, 1000, PacketType.String);


    공시(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
