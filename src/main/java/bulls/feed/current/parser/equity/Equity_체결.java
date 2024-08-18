package bulls.feed.current.parser.equity;

import bulls.annotation.Hint;
import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

/**
 * 주식, ELW, ETF 포함
 */
@Hint(info = "ELW 포함")
public enum Equity_체결 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(22, 2, PacketType.String),
    price(34, 9, PacketType.Integer),
    amount(43, 10, PacketType.Integer),
    sessionId(53, 2, PacketType.String),
    openPrice(55, 9, PacketType.Integer),
    highPrice(64, 9, PacketType.Integer),
    lowPrice(73, 9, PacketType.Integer),
    totalAmount(82, 12, PacketType.Integer),
    totalPrice(94, 18, PacketType.Integer),
    buySellSign(112, 1, PacketType.String), //1:매도 2:매수
    lpOwnAmount(120, 15, PacketType.Integer),
    askPrice(135, 9, PacketType.Integer),
    bidPrice(144, 9, PacketType.Integer),
    krxTime(114, 6, PacketType.Integer),
    isPriceSameWithFirstBidAsk(113, 1, PacketType.Integer); //0:판단불가 1:일치 2:불일치

    Equity_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

}
