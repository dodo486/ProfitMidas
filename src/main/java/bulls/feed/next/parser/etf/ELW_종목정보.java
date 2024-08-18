package bulls.feed.next.parser.etf;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum ELW_종목정보 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(13, 12, PacketType.String), //종목코드(13, 12, PacketType.String),
    발행사(31, 80, PacketType.String), //발행시장참가자명(31, 80, PacketType.String),
    발행사코드(191, 5, PacketType.String), //발행시장참가자번호(191, 5, PacketType.String),
    기초자산1(211, 12, PacketType.String), //기초자산종목코드1(211, 12, PacketType.String),
    //"1:유가증권 2:코스닥 3:섹터 4:GICS 8:MF(매경) 9:해외
    //S:S&P/KRX B:채권 C:통화 M:상품 R:부동산  2009.07.27
    //*기초자산이 지수일 경우만 해당
    //*구명칭:ELW기초자산시장구분코드"
    지수소속시장구분코드(325, 1, PacketType.String),
    //"ELW기초자산시장구분코드가 유가증권일 경우는 유가증권의
    //지수업종코드, 코스닥일 경우는 코스닥의 업종코드,
    //해외시장일 경우는 해외지수에 대한 별도의 코드정보
    //※코드값모음 - 대상지수업종코드표 참조"
    ELW지수업종코드(326, 3, PacketType.String),
    //C:콜 P:풋 Z:기타
    콜풋(329, 1, PacketType.String), //권리유형코드(342, 1, PacketType.String),
    최종거래일자(345, 8, PacketType.String), //최종거래일자(345, 8, PacketType.String),
    전환비율(574, 13, PacketType.Float), //ELW전환비율(574, 13, PacketType.Integer),
    만기평가방식(709, 200, PacketType.String), //만기평가가격방식(709, 200, PacketType.String),
    LP보유수량(910, 15, PacketType.Integer); //ELW_LP보유수량(910, 15, PacketType.Integer),


    ELW_종목정보(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
