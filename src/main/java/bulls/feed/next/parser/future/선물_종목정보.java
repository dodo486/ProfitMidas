package bulls.feed.next.parser.future;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

/**
 * 주식 선물, 섹터 미니 선물도 포함
 */
public enum 선물_종목정보 implements FeedParser {

    trCode(0, 5, PacketType.String),
    isinCode(18, 12, PacketType.String),
    productId(46, 11, PacketType.String),  //상품ID(46, 11, PacketType.String),
    code(57, 9, PacketType.String),  //종목단축코드(57, 9, PacketType.String),
    productName(56, 80, PacketType.String),  //종목명(66, 80, PacketType.String),
    기준가(397, 11, PacketType.Float),  //기준가격(397, 11, PacketType.Integer),
    결제월번호(454, 3, PacketType.Integer), //결제월일련번호(454, 3, PacketType.Integer),
    만기일자(457, 8, PacketType.String),  //만기일자(457, 8, PacketType.String),  -- 선물용
    거래승수(506, 22, PacketType.Float),  //거래승수(506, 22, PacketType.Float),  -- 선물용

    underlyingIsinCode(543, 12, PacketType.String), // 기초자산종목코드(543, 12, PacketType.String),
    underlyingClosingPrice(555, 11, PacketType.Float),  //기초자산종가(555, 11, PacketType.Integer),


    기초자산ID(408, 3, PacketType.String), // 기초자산ID(408, 3, PacketType.String), K2I:KOSPI200지수, KQI:코스닥150, Sxx:개별주식, BM3:신3년국채, BM5:신5년국채, BMA:신10년국채, CDR:CD, USD:미국달러, JPY:일본엔, EUR:유럽유로, GLD:금, LHG:돈육20140303
    소속상품군(1082, 4, PacketType.String),  //기초자산상품군ID(1082, 4, PacketType.String),
    기초자산시장ID(1177, 3, PacketType.String), // 기초자산시장ID(1177, 3, PacketType.String),	기초자산시장ID는 기초자산이 KRX에 상장된 경우에만 채워짐.K200선물옵션:STK코스닥150선물:KSQ주식선물옵션:STK/KSQ 상품선물옵션:SPACE변동성지수선물:STK섹터지수선물:STK미니K200선물옵션:STK
    미결제한도수량(1067, 15, PacketType.Integer),  //주식선물미결제한도수량(1067, 15, PacketType.Integer),
    전일거래수량(970, 15, PacketType.Integer),  // 전일체결건수(970, 15, PacketType.Integer),
    전일거래대금(997, 22, PacketType.Float),  //전일누적거래대금(997, 22, PacketType.Float),  전일총누적거래대금(1034, 22, PacketType.Float),
    CD금리(1056, 11, PacketType.Float),  //CD금리(1056, 11, PacketType.Integer),
    전일미결제약정수량(825, 12, PacketType.Integer), //전일미결제약정수량(825, 12, PacketType.Integer),
    거래단위(484, 22, PacketType.Float), // 거래단위(484, 22, PacketType.Float), 1계약에 해당하는 기초자산수량예) 999999999.99999999=> Exture에서는 1이었으나, Exture+에서는 500000임 20140303

    ATMPrice(530, 11, PacketType.Float), //등가격(530, 11, PacketType.Integer),
    잔존일수(566, 8, PacketType.Integer),  //잔존일수(566, 8, PacketType.Integer),
    전일종가(748, 11, PacketType.Float), //전일종가(748, 11, PacketType.Integer),
    전일시가(760, 11, PacketType.Float),  // 이전일자시가(760, 11, PacketType.Integer),
    전일고가(771, 11, PacketType.Float),  //이전일자고가(771, 11, PacketType.Integer),
    전일저가(782, 11, PacketType.Float), // 이전일자저가(782, 11, PacketType.Integer),

    가격제한1단계상한가(331, 11, PacketType.Float),  //가격제한1단계상한가(331, 11, PacketType.Integer),
    가격제한1단계하한가(364, 11, PacketType.Float),  //가격제한1단계하한가(364, 11, PacketType.Integer),
    가격제한2단계상한가(342, 11, PacketType.Float),  //가격제한2단계상한가(342, 11, PacketType.Integer),
    가격제한2단계하한가(375, 11, PacketType.Float),  //가격제한2단계하한가(375, 11, PacketType.Integer),
    가격제한3단계상한가(353, 11, PacketType.Float),  //가격제한3단계상한가(353, 11, PacketType.Integer),
    가격제한3단계하한가(386, 11, PacketType.Float),  //가격제한3단계하한가(386, 11, PacketType.Integer),

    spreadRecentCode(414, 12, PacketType.String),
    spreadNextCode(426, 12, PacketType.String),

    ATM구분코드(730, 1, PacketType.String); // 0:선물 1:ATM 2:ITM 3:OTM   ATM구분코드(730, 1, PacketType.String),

    선물_종목정보(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
