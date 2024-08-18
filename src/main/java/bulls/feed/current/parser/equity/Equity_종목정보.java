package bulls.feed.current.parser.equity;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Equity_종목정보 implements FeedParser {
    trCode(0, 5, PacketType.String),
    시장구분(4, 1, PacketType.Integer),
    isinCode(5, 12, PacketType.String),
    shortCode(25, 9, PacketType.String),
    productName(34, 40, PacketType.String),
    증권그룹ID(127, 2, PacketType.String), //ST:주권 MF:증권투자회사 RT:부동산투자회사 SC:선박투자회사 IF:사회간접자본투융자회사 DR:주식예탁증서 EW:ELW EF:ETF SW:신주인수권증권 SR:신주인수권증서 BC:수익증권  FE:해외ETF FS:외국주권  EN:ETN
    관리종목여부(143, 1, PacketType.String),
    거래정지여부(146, 1, PacketType.String),
    KOSPI200섹터업종(166, 1, PacketType.String), //0:업종미분류, 1:건설, 2:중공업, 3:철강소재, 4:에너지화학, 5:정보통신, 6:금융, 7:필수소비재, 8: 자유소비재 9:산업재  A:건강관리
    시가총액규모코드(167, 1, PacketType.String),//유가(0:제외 1:대 2:중 3:소)코스닥(0:제외 1:KOSDAQ100 2:KOSDAQmid300 3:KOSDAQsmall)※증권그룹ID ST,MF,RT,SC,IF만 해당
    KOSPI여부(174, 1, PacketType.String),//Y, N(유가)KOSPI여부-> (유가)KOSPI지수종목여부,(코스닥)KOSDAQ지수종목여부 2018.12.10
    기준가격(190, 9, PacketType.Integer),//
    전일종가구분코드(199, 1, PacketType.String),//1:실세 2:기세 3:거래무 4:시가기준가종목의 기세
    전일종가(200, 9, PacketType.Integer),//
    전일누적체결수량(209, 12, PacketType.Integer),//
    전일누적거래대금(221, 18, PacketType.Integer),//
    상한가(239, 9, PacketType.Integer),//
    하한가(248, 9, PacketType.Integer),//
    대용가격(257, 9, PacketType.Integer),//※ST,FS,DR,MF,RT,SC,IF,ET,FE,BC,EN 만 해당 2014.11.17
    액면가(266, 12, PacketType.Integer),//9(9)V9(3) 외국주권일 경우 소숫점셋째자리까지 표현가능코스닥의 각국의 최소화폐단위 표시는 유가기준으로 통일※ST,FS,RT,SC,BC만 해당
    발행가격(278, 9, PacketType.Integer),//ELW, 신주인수권증서 포함
    상장일자(287, 8, PacketType.String),//YYYYMMDD
    상장주식수(295, 15, PacketType.Integer),
    ELW행사종료일(388, 8, PacketType.String),
    ELW행사가격(396, 12, PacketType.Integer),
    ETF복제방법구분코드(583, 1, PacketType.String),//ETF의 기초자산 복제방법구분코드 2013.03.18 추가P:실물복제S:합성복제A:Active   (2015.11.23 구분코드추가)
    ETP상품구분코드(619, 1, PacketType.String),//"(2015.11.23 추가)    ETP상품구분코드 ## 코드값 ##            1. ETF(투자회사형)2. ETF(수익증권형)3. ETN4. 손실제한 ETN            (2017.03.27;ELS형 ETN->손실제한 ETN)"
    지수산출기관코드(620, 2, PacketType.String),
    지수시장분류ID(622, 6, PacketType.String), //"(2015.11.23 추가) *대분류(2)+중분류(2)+소분류(2)    값이 생략되는 분류의 자리는 '00'을 표기    중,소분류 지정시 상위 분류의 값은 생략 할 수 없음"
    지수일련번호(628, 3, PacketType.String),
    추적지수레버리지인버스구분코드(631, 2, PacketType.String),//"(2015.11.23 추가)## 코드값 ##    P1:일반(1)    P2:2X 레버리지(2)    N1:1X 인버스(-1)    N2:2X 인버스(-2)"
    참조지수레버리지인버스구분코드(633, 2, PacketType.String),//"(2015.11.23 추가)## 코드값 ##    P1:일반(1)    P2:2X 레버리지(2)    N1:1X 인버스(-1)    N2:2X 인버스(-2)"
    지수자산분류ID1(635, 6, PacketType.String),//"(2015.11.23 추가) *대분류(2)+중분류(2)+소분류(2)    값이 생략되는 분류의 자리는 '00'을 표기    중,소분류 지정시 상위 분류의 값은 생략 할 수 없음"
    지수자산분류ID2(641, 6, PacketType.String),//"(2015.11.23 추가) *대분류(2)+중분류(2)+소분류(2)    값이 생략되는 분류의 자리는 '00'을 표기    중,소분류 지정시 상위 분류의 값은 생략 할 수 없음"
    LP주문가능여부(647, 1, PacketType.String),
    KOSDAQ150지수종목여부(648, 1, PacketType.String),
    저유동성여부(649, 1, PacketType.String),
    이상급등여부(650, 1, PacketType.String),
    KRX300지수여부(651, 1, PacketType.String),
    상한수량(652, 16, PacketType.Integer); //

    Equity_종목정보(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
