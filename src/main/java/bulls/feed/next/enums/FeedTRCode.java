package bulls.feed.next.enums;

import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedParser;
import bulls.feed.current.data.cmtyFuture.상품선물_종목정보Feed;
import bulls.feed.current.data.kospiFuture.선물_종목정보Feed;
import bulls.feed.current.data.kospiOption.Option_종목정보Feed;
import bulls.feed.current.data.statistics.시장통계_선물_투자자데이터Feed;
import bulls.feed.current.data.statistics.시장통계_옵션_투자자데이터Feed;
import bulls.feed.current.data.stockFuture.SF_종목정보Feed;
import bulls.feed.current.data.stockOption.SO_종목정보Feed;
import bulls.feed.current.parser.kospiFuture.선물_종목정보;
import bulls.feed.current.parser.kospiOption.Option_종목정보;
import bulls.feed.next.parser.statistics.시장통계_선물_투자자데이터;
import bulls.feed.next.parser.statistics.시장통계_옵션_투자자데이터;
import bulls.feed.next.data.equity.Equity_종목정보Feed;
import bulls.feed.next.data.etc.UndefinedFeed;
import bulls.feed.next.data.etc.공시_Feed;
import bulls.feed.next.data.etc.지수_Feed;
import bulls.feed.next.data.etc.지수_예상_Feed;
import bulls.feed.next.data.option.*;
import bulls.feed.next.data.statistics.*;
import bulls.feed.next.parser.equity.Equity_종목정보;
import bulls.feed.next.parser.etc.NeedToImplement;
import bulls.feed.next.parser.etc.공시;
import bulls.feed.next.parser.etc.지수;
import bulls.feed.next.parser.option.Option_민감도;
import bulls.feed.next.parser.statistics.*;
import bulls.log.DefaultLogger;
import bulls.server.enums.ServerLocation;
import bulls.staticData.ProdType.ProdType;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public enum FeedTRCode {


    /**
     * 시세종가 - 시고종저 포함
     */
    // TRCODE , Class (filler가 없음), feed 통과
    C101S("C101S".getBytes(), TRDescription.코스피_통계, 종목별_투자자별_종가통계.class, 종목별_투자자별_종가통계Feed.class),
    C101Q("C101Q".getBytes(), TRDescription.코스닥_통계, 종목별_투자자별_종가통계.class, 종목별_투자자별_종가통계Feed.class),
    C102S("C102S".getBytes(), TRDescription.ELW_통계, 종목별_투자자별_종가통계.class, 종목별_투자자별_종가통계Feed.class),
    C103S("C103S".getBytes(), TRDescription.ELW_통계, 종목별_투자자별_종가통계.class, 종목별_투자자별_종가통계Feed.class),
    C104S("C104S".getBytes(), TRDescription.ELW_통계, 종목별_투자자별_종가통계.class, 종목별_투자자별_종가통계Feed.class),

    /**
     * 지수
     * D2 : data 구분 , 01 : 주식 , 1: 유가증권 , 2: 코스닥
     */
    // TR_CODE OK, 지수.class 필드가 안맞음, 지수_예상_Feed/지수Feed OK
    IA000("IA000".getBytes(), TRDescription.지수, 지수.class, 지수_Feed.class), // Kospi 200 지수, 코스닥 150 지수, K200 섹터 지수, KRX300 섹터 지수, 코스피 배당성장 50, 코스피 고배당 50, 변동성 지수, KRX Mid 200 지수 (+ K-뉴딜)
    IB000("IB000".getBytes(), TRDescription.지수, 지수.class, 지수_예상_Feed.class), //Kospi 200 예상지수, 코스닥 150 예상지수, K200 섹터 예상지수, KRX300 섹터 예상지수, 코스피 배당성장 50 예상지수, 코스피 고배당 50 예상지수, KRX Mid 200 예상지수 (+ K-뉴딜 예상)

    // TR_CODE OK, Option_민감도.class OK,  Option_민감도Feed.class OK
    N703F("N703F".getBytes(), TRDescription.K200옵션_민감도, Option_민감도.class, Option_민감도Feed.class),
    N712F("N712F".getBytes(), TRDescription.미니K200옵션_민감도, Option_민감도.class, Option_민감도Feed.class),
    N715F("N715F".getBytes(), TRDescription.코스닥150옵션_민감도, Option_민감도.class, Option_민감도Feed.class),
    N705F("N705F".getBytes(), TRDescription.주식옵션_민감도, Option_민감도.class, Option_민감도Feed.class),

    // TR_CODE OK, NeedToImplement.class OK, UndefinedFeed.class OK
    C602S("C602S".getBytes(), TRDescription.ELW_통계, NeedToImplement.class, UndefinedFeed.class),
    C702S("C702S".getBytes(), TRDescription.ELW_민감도, NeedToImplement.class, UndefinedFeed.class),

    /**
     * 시장통계
     */
    // TR_CODE OK, 시장통계_프로그램매매투자자별,class OK, 시장통계_프로그램매매투자자별Feed OK
    P001S("P001S".getBytes(), TRDescription.코스피_통계, 시장통계_프로그램매매투자자별.class, 시장통계_프로그램매매투자자별Feed.class), // 유가증권 프로그램매매매 투자자별
    P001Q("P001Q".getBytes(), TRDescription.코스닥_통계, 시장통계_프로그램매매투자자별.class, 시장통계_프로그램매매투자자별Feed.class), // 코스닥 프로그램매매매 투자자별

    // TR_CODE OK, 시장통계_파생_투자자데이터.class OK, 시장통계_파생_투자자데이터Feed.class OK
    H101F("H101F".getBytes(), TRDescription.K200선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), // K200선물_투자자 데이터
    H104F("H104F".getBytes(), TRDescription.주식선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), // 주식선물_투자자 데이터
    H109F("H109F".getBytes(), TRDescription.섹터지수선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), // 섹터지수선물_투자자 데이터
    H102F("H102F".getBytes(), TRDescription.코스닥150선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), // 코스닥150선물_투자자 데이터
    H111F("H111F".getBytes(), TRDescription.미니K200선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), //
    H113F("H113F".getBytes(), TRDescription.KRX300선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), //KRX300

    // TR_CODE OK, 시장통계_파생_투자자데이터.class OK, 시장통계_파생_투자자데이터Feed.class OK
    H103F("H103F".getBytes(), TRDescription.K200옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //
    H116F("H116F".getBytes(), TRDescription.K200위클리옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //
    H105F("H105F".getBytes(), TRDescription.주식옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //
    H112F("H112F".getBytes(), TRDescription.미니K200옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //
    H115F("H115F".getBytes(), TRDescription.코스닥150옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //



    // TR_CODE 못구함- 안쓰이니까 skip
//    B9011("B9011".getBytes(), TRDescription.코스피_통계, 시장통계_거래원.class, 시장통계_거래원Feed.class), // 코스피 거래원
//    B9021("B9021".getBytes(), TRDescription.ELW_통계, 시장통계_거래원.class, 시장통계_거래원Feed.class), // ELW 거래원
//    B9012("B9012".getBytes(), TRDescription.코스닥_통계, 시장통계_거래원.class, 시장통계_거래원Feed.class), // 코스닥 거래원

    /**
     * 종목정보 - Batch
     */
    // Feed는 정보가 많아서 유지 -> Feed안에서 쓰는 파서명도 유지해야됨
    // TR_CODE OK, 종목정보.class OK , Feed OK
    A003F("A003F".getBytes(), TRDescription.종목배치, Option_종목정보.class, Option_종목정보Feed.class), // K200 옵션
    A016F("A016F".getBytes(), TRDescription.종목배치, Option_종목정보.class, Option_종목정보Feed.class), // 위클리 옵션
    A012F("A012F".getBytes(), TRDescription.종목배치, Option_종목정보.class, Option_종목정보Feed.class), // 미니
    A004F("A004F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, SF_종목정보Feed.class), // 주식선물
    A006F("A006F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 상품선물_종목정보Feed.class), // 상품선물
    A010F("A010F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 상품선물_종목정보Feed.class), // 상품선물
    A001F("A001F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // K200 선물
    A011F("A011F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // 미니 선물
    A009F("A009F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // 섹터 선물
    A002F("A002F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // 코스닥 선물
    A014F("A014F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // 유로스탁스50 선물
    A005F("A005F".getBytes(), TRDescription.종목배치, Option_종목정보.class, SO_종목정보Feed.class), // 주식옵션
    A013F("A013F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // KRX300 선물
    A015F("A015F".getBytes(), TRDescription.종목배치, Option_종목정보.class, Option_종목정보Feed.class), // KQ150옵션
    A008F("A008F".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // vkospi 선물


    // TR_CODE OK, Equity_종목정보.class OK, Equity_종목정보Feed OK
    A001S("A001S".getBytes(), TRDescription.종목배치, Equity_종목정보.class, Equity_종목정보Feed.class), // 코스피_종목배치
    A002S("A002S".getBytes(), TRDescription.종목배치, Equity_종목정보.class, Equity_종목정보Feed.class), // 코스피_종목배치
    A003S("A003S".getBytes(), TRDescription.종목배치, Equity_종목정보.class, Equity_종목정보Feed.class), // 코스피_종목배치
    A004S("A004S".getBytes(), TRDescription.종목배치, Equity_종목정보.class, Equity_종목정보Feed.class), // 코스피_종목배치
    A001Q("A001Q".getBytes(), TRDescription.종목배치, Equity_종목정보.class, Equity_종목정보Feed.class), // 코스닥_종목배치

//    // TR_CODE OK, ELW_종목정보.class 못찾은거 많음,
//    A102S("A102S".getBytes(), TRDescription.종목배치, ELW_종목정보.class, ELW_종목정보Feed.class), // ELW 부가
//    // TR_CODE OK, ETF_사무수탁배치.class OK, ETF_사무수탁배치Feed OK
//    N803S("N803S".getBytes(), TRDescription.종목배치, ETF_사무수탁배치.class, ETF_사무수탁배치Feed.class), // etf 배치 (cu 수량)
//
    //공시
    // TR_CODE OK, 공시.class OK, 공시_Feed OK
    F000S("F000S".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class), // 공시(실시간_코스피_주식)
    F000Q("F000Q".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class), // 공시(실시간_코스닥_주식)
    F000X("F000X".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class), // 공시(실시간_코넥스_주식)
    F0909("F0909".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class), // 공시(실시간_기타)
    E900S("E900S".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class), // 공시(배치_코스피_주식)
    E900Q("E900Q".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class), // 공시(배치_코스닥_주식)
    E900X("E900X".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class), // 공시(배치_코넥스_주식)
    E9909("E9909".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class), // 공시(배치_기타)

    NotMath("N / A".getBytes(), TRDescription.Unidentified, NeedToImplement.class, UndefinedFeed.class);


    /**
     * 부산 파생 시세
     */


    static {
        ArrayList<FeedTRCode> trList = new ArrayList<>();
        for (FeedTRCode tr : FeedTRCode.values()) {
            if (tr.description.toString().startsWith("부산_"))
                trList.add(tr);
        }
        pusanFeedArray = trList.toArray(new FeedTRCode[trList.size()]);
    }

    public static final FeedTRCode[] pusanFeedArray;

    private static final int LEN = 5;

    private final byte[] trBytes;

    private final String trCodeStr;
    private final Class<? extends Feed> feedClass;

    private final Class<? extends FeedParser> parser;

    private Constructor<? extends Feed> constructor = null;


    private final TRDescription description;


    FeedTRCode(byte[] trByte, TRDescription description, Class<? extends FeedParser> parser, Class<? extends Feed> feedClass) {
        this.trBytes = trByte; // enough with reference. don't have to copy all since it's in enum.
        this.trCodeStr = new String(trBytes);
        this.feedClass = feedClass;
        this.parser = parser;
        this.description = description;
        try {
            constructor = feedClass.getConstructor(FeedTRCode.class, byte[].class);
        } catch (NoSuchMethodException e) {
            DefaultLogger.logger.error("error found", e);
        }
    }


    public TRDescription getDescription() {
        return description;
    }

    public static FeedTRCode matchTR(String trCodeStr) {
        for (FeedTRCode tr : FeedTRCode.values()) {
            //서울만 매칭
            if (tr.getDescription().getLocation() == ServerLocation.PUSAN)
                continue;
            if (tr.trCodeStr.equals(trCodeStr))
                return tr;
        }
        return NotMath;
    }

    public static FeedTRCode matchTR(byte[] trBytes) {
        for (FeedTRCode tr : FeedTRCode.values()) {

            int i = 0;
            while (i < LEN) {
                if (tr.trBytes[i] != trBytes[i]) {
                    break;
                }
                i++;
            }
            if (i == LEN)
                return tr;
        }
        return NotMath;
    }

    public static FeedTRCode matchTRPusan(byte[] trBytes) {
        for (FeedTRCode tr : pusanFeedArray) {

            int i = 0;
            while (i < LEN) {
                if (tr.trBytes[i] != trBytes[i]) {
                    break;
                }
                i++;
            }
            if (i == LEN)
                return tr;
        }
        return NotMath;
    }

    public static FeedTRCode matchTRPusan(String trCodeStr) {
        for (FeedTRCode tr : pusanFeedArray) {
            if (tr.trCodeStr.equals(trCodeStr))
                return tr;
        }
        return NotMath;
    }

    // 주어진 Feed 클래스로 파싱 가능한 모든 TR 들의 리스트를 리턴
    // 주어진 Feed 클래스와 FeedTRCode enum 생성자의 3번째 parameter 이름이 같은 모든 TR 리턴
    public static FeedTRCode[] getTRListFromFeedClassList(Class<? extends Feed>[] feedList) {
        ArrayList<FeedTRCode> list = new ArrayList<>();
        for (Class<? extends Feed> feed : feedList) {
            for (FeedTRCode tr : FeedTRCode.values()) {
                if (tr.feedClass == feed) {
                    list.add(tr);
                }
            }
        }

        return list.toArray(new FeedTRCode[0]);
    }

    public static ArrayList<FeedTRCode> getTRListFromTRName(TRDescription description) {
        ArrayList<FeedTRCode> list = new ArrayList<>();
        for (FeedTRCode tr : FeedTRCode.values()) {

            if (tr.description == description) {
                list.add(tr);
            }
        }
        return list;
    }

    /**
     * 주어진 ProdType과 Location에 해당되는 FeedTrCode 리스트를 조회한다.
     *
     * @param pt  조회하려는 ProdType
     * @param src 조회하려는 location. seoul 또는 pusan
     * @return FeedTRCode 리스트
     */
    public static ArrayList<FeedTRCode> getSiseTRListWithProdType(ProdType pt, String src) {
        ArrayList<FeedTRCode> list = new ArrayList<>();
        for (FeedTRCode tr : FeedTRCode.values()) {
            if (src.equals("seoul") && tr.toString().startsWith("P_") || src.equals("pusan") && !tr.toString().startsWith("P_"))
                continue;
            String trCode = tr.getTrCodeStr();
            FeedDataType fdt = FeedDataType.parseFromTrCode(trCode);
            if (fdt == FeedDataType.BidAsk || fdt == FeedDataType.Price || fdt == FeedDataType.PriceBidAsk) {
                if (pt.isK200Fut() || pt.isK200FutSP()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.IndexDeriv && FeedInfoType.Kospi200Fut.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isK200Opt()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.IndexDeriv && FeedInfoType.Kospi200Opt.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isK200MiniFut() || pt.isK200MiniFutSP()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.IndexDeriv && FeedInfoType.Kospi200MiniFut.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isK200MiniOpt()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.IndexDeriv && FeedInfoType.Kospi200MiniOpt.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isKQ150Fut() || pt.isKQ150FutSP()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.IndexDeriv && FeedInfoType.Kosdaq150Fut.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isKQ150Opt()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.IndexDeriv && FeedInfoType.Kosdaq150Opt.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isKRX300Fut() || pt.isKRX300FutSP()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.IndexDeriv && FeedInfoType.Krx300Fut.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isStockFut() || pt.isStockFutSP()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.StockDeriv && FeedInfoType.StockFut.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isStockOpt()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.StockDeriv && FeedInfoType.StockOpt.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isEquityStockKOSPI() || pt.isEquityELW()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.KOSPI && FeedInfoType.Equity.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isEquityStockKOSDAQ()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.KOSDAQ && FeedInfoType.Equity.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                } else if (pt.isSectorFut() || pt.isSectorFutSP()) {
                    if (FeedMarketType.parseFromTrCode(trCode) == FeedMarketType.IndexDeriv && FeedInfoType.SectorFut.isSameTypeByTrCode(trCode)) {
                        list.add(tr);
                    }
                }
            }
        }
        return list;
    }

    public byte[] getTrBytes() {
        return trBytes;
    }

    public Constructor<? extends Feed> getConstructor() {
        return constructor;
    }

    public Class<? extends FeedParser> getParser() {
        return parser;
    }

    public String getTrCodeStr() {
        return trCodeStr;
    }

}

