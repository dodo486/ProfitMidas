package bulls.feed.current.parser.kospiFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

/**
 * 주식 선물, 섹터 미니 선물도 포함
 */
public enum 선물_종목정보 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(18, 12, PacketType.String),
    productId(36, 11, PacketType.String),
    code(47, 9, PacketType.String),
    productName(56, 80, PacketType.String),
    기준가(396, 12, PacketType.Integer),
    결제월번호(454, 3, PacketType.Integer),
    만기일자(457, 8, PacketType.String),
    거래승수(500 + 3, 21, PacketType.Float),

    underlyingIsinCode(537 + 3, 12, PacketType.String),

    underlyingClosingPrice(549 + 3, 12, PacketType.Float),


    기초자산ID(408, 3, PacketType.String),
    소속상품군(1075 + 3, 4, PacketType.String),
    기초자산시장ID(1172 + 3, 3, PacketType.String),
    미결제한도수량(1060 + 3, 15, PacketType.Integer),
    전일거래수량(986 + 3, 12, PacketType.Integer),
    전일거래대금(998 + 3, 22, PacketType.Integer),
    CD금리(1054 + 3, 6, PacketType.Integer),
    전일미결제약정수량(816 + 3, 10, PacketType.Integer),
    거래단위(483, 17 + 3, PacketType.Float),

    ATMPrice(523 + 3, 12, PacketType.Integer),
    잔존일수(561 + 3, 7, PacketType.Integer),
    전일종가(733 + 3, 12, PacketType.Integer),
    전일시가(747 + 3, 12, PacketType.Integer),
    전일고가(760 + 3, 12, PacketType.Integer),
    전일저가(773 + 3, 12, PacketType.Integer),

    가격제한1단계상한가(319, 12, PacketType.Integer),
    가격제한1단계하한가(332, 12, PacketType.Integer),
    가격제한2단계상한가(345, 12, PacketType.Integer),
    가격제한2단계하한가(358, 12, PacketType.Integer),
    가격제한3단계상한가(371, 12, PacketType.Integer),
    가격제한3단계하한가(384, 12, PacketType.Integer),

    spreadRecentCode(414, 12, PacketType.String),
    spreadNextCode(426, 12, PacketType.String),

    ATM구분코드(715 + 3, 1, PacketType.Integer); // 0:선물 1:ATM 2:ITM 3:OTM


    선물_종목정보(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
