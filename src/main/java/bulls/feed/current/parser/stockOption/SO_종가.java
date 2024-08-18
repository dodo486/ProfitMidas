package bulls.feed.current.parser.stockOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum SO_종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    price(24, 7, PacketType.Integer),
    totalContractAmount(32, 7, PacketType.Integer),
    totalContractPrice(39, 15, PacketType.Integer),
    bidPrice(61, 7, PacketType.Integer),
    bidAmount(68, 7, PacketType.Integer),
    askPrice(208, 7, PacketType.Integer),
    askAmount(215, 7, PacketType.Integer);

    //20180730 변경
//    trCode(0, 5 , PacketType.String ),
//    isinCode(5, 12 , PacketType.String),
//    price(23 , 7 , PacketType.Integer),
//    bidPrice(60 ,7 , PacketType.Integer),
//    bidAmount(67 , 7 , PacketType.Integer),
//    askPrice(207 ,7 , PacketType.Integer),
//    askAmount(214 , 7 , PacketType.Integer);


    SO_종가(int offSet, int length, PacketType type) {
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
