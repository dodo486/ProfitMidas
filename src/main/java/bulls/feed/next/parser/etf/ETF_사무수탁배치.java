package bulls.feed.next.parser.etf;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum ETF_사무수탁배치 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String), // 종목코드(5, 12, PacketType.String),
    전일NAV(65, 9, PacketType.Float), // ETF최종순자산가치(65, 9, PacketType.Integer),
    ETF_CU수량(113, 8, PacketType.Integer); //ETF CU수량(113, 8, PacketType.Integer),


    ETF_사무수탁배치(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
