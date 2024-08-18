package bulls.feed.current.parser.equity;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Equity_임의종료 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    //code(8, 6, PacketType.String),
    boardId(22, 2, PacketType.String),
    randomEndCode(24, 1, PacketType.String);

    Equity_임의종료(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
