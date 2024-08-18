package bulls.feed.current.parser.kosdaqOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum KQOption_체결 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(21, 2, PacketType.String),

    price(23, 5, PacketType.Integer),
    amount(29, 6, PacketType.Integer),
    totalAmount(69, 7, PacketType.Integer),
    totalPrice(76, 11, PacketType.Integer),
    buySellSign(94, 1, PacketType.String); // 1:매도 2:매수

    KQOption_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
