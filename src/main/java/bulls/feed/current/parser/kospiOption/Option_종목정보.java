package bulls.feed.current.parser.kospiOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Option_종목정보 implements FeedParser {


    trCode(0, 5, PacketType.String),
    isinCode(18, 12, PacketType.String),
    code(20, 9, PacketType.String),
    productId(36, 11, PacketType.String),
    기준가(396, 12, PacketType.Integer),
    productName(56, 40, PacketType.String),
    결제월번호(454, 3, PacketType.Integer),
    matDate(457, 8, PacketType.String),
    multiplier(500 + 3, 21, PacketType.Float),    // .xx 식의 소숫점 두자리 식으로만 파싱하게 되어있으므로 일단 블락..
    strikePrice(465, 17, PacketType.Float),
    strikePriceDisp(698 + 3, 17, PacketType.Float),
    underlyingIsinCode(537 + 3, 12, PacketType.String),

    underlyingClosingPrice(549 + 3, 12, PacketType.Float),


    기초자산ID(408, 3, PacketType.String), // K2I:KOSPI200지수, KQI:코스닥150, Sxx:개별주식, BM3:신3년국채, BM5:신5년국채, BMA:신10년국채, CDR:CD, USD:미국달러, JPY:일본엔, EUR:유럽유로, GLD:금, LHG:돈육20140303
    소속상품군(1075 + 3, 4, PacketType.String),
    기초자산시장ID(1172 + 3, 3, PacketType.String), // 기초자산시장ID는 기초자산이 KRX에 상장된 경우에만 채워짐.K200선물옵션:STK코스닥150선물:KSQ주식선물옵션:STK/KSQ 상품선물옵션:SPACE변동성지수선물:STK섹터지수선물:STK미니K200선물옵션:STK
    미결제한도수량(1060 + 3, 15, PacketType.Integer),
    전일거래수량(986 + 3, 12, PacketType.Integer),
    전일거래대금(998 + 3, 22, PacketType.Integer),
    CD금리(1054 + 3, 6, PacketType.Integer),
    전일미결제약정수량(816 + 3, 10, PacketType.Integer),
    거래단위(483, 17 + 3, PacketType.Float), // 1계약에 해당하는 기초자산수량예) 999999999.99999999=> Exture에서는 1이었으나, Exture+에서는 500000임 20140303

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

    결제주(1239 + 3, 2, PacketType.String),

    ATM구분코드(715 + 3, 1, PacketType.Integer); // 0:선물 1:ATM 2:ITM 3:OTM


    Option_종목정보(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}