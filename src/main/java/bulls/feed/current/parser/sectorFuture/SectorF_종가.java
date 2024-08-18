package bulls.feed.current.parser.sectorFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SectorF_종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),

    boardId(23, 2, PacketType.String),
    priceSign(25, 1, PacketType.String),
    price(26, 8, PacketType.Integer),

    totalContractAmount(35, 11, PacketType.Integer),
    totalContractPrice(46, 15, PacketType.Integer),

    bidPrice(82, 8, PacketType.Integer),
    bidAmount(90, 8, PacketType.Integer),
    askPrice(176, 8, PacketType.Integer),
    askAmount(184, 8, PacketType.Integer);


    SectorF_종가(int offSet, int length, PacketType type) {
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
