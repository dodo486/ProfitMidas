package bulls.feed.current.enums;

import bulls.annotation.Hint;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedParser;
import bulls.feed.abstraction.TRCodeInformation;
import bulls.feed.current.data.EuroStoxx50.EuroStoxx50_체결Feed;
import bulls.feed.current.data.EuroStoxx50.EuroStoxx50_체결호가Feed;
import bulls.feed.current.data.EuroStoxx50.EuroStoxx50_호가Feed;
import bulls.feed.current.data.cmtyFuture.*;
import bulls.feed.current.data.equity.*;
import bulls.feed.current.data.etc.UndefinedFeed;
import bulls.feed.current.data.etc.공시_Feed;
import bulls.feed.current.data.etc.지수_Feed;
import bulls.feed.current.data.etc.지수_예상_Feed;
import bulls.feed.current.data.etf.*;
import bulls.feed.current.data.kosdaqFuture.*;
import bulls.feed.current.data.kosdaqOption.*;
import bulls.feed.current.data.kospiFuture.*;
import bulls.feed.current.data.kospiOption.*;
import bulls.feed.current.data.krx300Future.KRX300Futures_종가Feed;
import bulls.feed.current.data.krx300Future.KRX300Futures_체결Feed;
import bulls.feed.current.data.krx300Future.KRX300Futures_체결호가Feed;
import bulls.feed.current.data.krx300Future.KRX300Futures_호가Feed;
import bulls.feed.current.data.pusan.*;
import bulls.feed.current.data.sectorFuture.*;
import bulls.feed.current.data.statistics.*;
import bulls.feed.current.data.stockFuture.*;
import bulls.feed.current.data.stockOption.*;
import bulls.feed.current.parser.cmtyFuture.CmtyFuture_종가;
import bulls.feed.current.parser.cmtyFuture.CmtyFuture_체결;
import bulls.feed.current.parser.cmtyFuture.CmtyFuture_체결호가;
import bulls.feed.current.parser.cmtyFuture.CmtyFuture_호가;
import bulls.feed.current.parser.equity.*;
import bulls.feed.current.parser.etc.NeedToImplement;
import bulls.feed.current.parser.etc.공시;
import bulls.feed.current.parser.etc.지수;
import bulls.feed.current.parser.etf.*;
import bulls.feed.current.parser.euroStoxx50.EuroStoxx50_체결;
import bulls.feed.current.parser.euroStoxx50.EuroStoxx50_체결호가;
import bulls.feed.current.parser.euroStoxx50.EuroStoxx50_호가;
import bulls.feed.current.parser.kosdaqFuture.KQFutures_종가;
import bulls.feed.current.parser.kosdaqFuture.KQ_체결;
import bulls.feed.current.parser.kosdaqFuture.KQ_체결호가;
import bulls.feed.current.parser.kosdaqFuture.KQ_호가;
import bulls.feed.current.parser.kosdaqOption.KQOption_종가;
import bulls.feed.current.parser.kosdaqOption.KQOption_체결;
import bulls.feed.current.parser.kosdaqOption.KQOption_체결호가;
import bulls.feed.current.parser.kosdaqOption.KQOption_호가;
import bulls.feed.current.parser.kospiFuture.*;
import bulls.feed.current.parser.kospiOption.*;
import bulls.feed.current.parser.krx300Future.KRX300Futures_종가;
import bulls.feed.current.parser.krx300Future.KRX300_체결;
import bulls.feed.current.parser.krx300Future.KRX300_체결호가;
import bulls.feed.current.parser.krx300Future.KRX300_호가;
import bulls.feed.current.parser.pusan.*;
import bulls.feed.current.parser.sectorFuture.*;
import bulls.feed.current.parser.statistics.*;
import bulls.feed.current.parser.stockFuture.*;
import bulls.feed.current.parser.stockOption.*;
import bulls.log.DefaultLogger;
import bulls.server.enums.ServerLocation;
import bulls.staticData.ProdType.ProdType;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public enum FeedTRCode implements TRCodeInformation {

    A3014("A3014".getBytes(), TRDescription.K200선물, Future_체결.class, Future_체결Feed.class),
    B6014("B6014".getBytes(), TRDescription.K200선물, Future_호가.class, Future_호가Feed.class),
    G7014("G7014".getBytes(), TRDescription.K200선물, Future_체결호가.class, Future_체결호가Feed.class),

    // 미니 선물
    A3124("A3124".getBytes(), TRDescription.미니K200선물, Future_체결.class, Future_체결Feed.class),
    B6124("B6124".getBytes(), TRDescription.미니K200선물, Future_호가.class, Future_호가Feed.class),
    G7124("G7124".getBytes(), TRDescription.미니K200선물, Future_체결호가.class, Future_체결호가Feed.class),

    // KRX300 선물
    A3164("A3164".getBytes(), TRDescription.KRX300선물, KRX300_체결.class, KRX300Futures_체결Feed.class),
    B6164("B6164".getBytes(), TRDescription.KRX300선물, KRX300_호가.class, KRX300Futures_호가Feed.class),
    G7164("G7164".getBytes(), TRDescription.KRX300선물, KRX300_체결호가.class, KRX300Futures_체결호가Feed.class),

    // SECTOR 선물
    A3104("A3104".getBytes(), TRDescription.섹터지수선물, SECTOR_체결.class, SectorF_체결Feed.class),
    B6104("B6104".getBytes(), TRDescription.섹터지수선물, SECTOR_호가.class, SectorF_호가Feed.class),
    G7104("G7104".getBytes(), TRDescription.섹터지수선물, SECTOR_체결호가.class, SectorF_체결호가Feed.class),

    // 변동성지수 선물
    A3094("A3094".getBytes(), TRDescription.변동성지수선물, SECTOR_체결.class, SectorF_체결Feed.class),
    B6094("B6094".getBytes(), TRDescription.변동성지수선물, SECTOR_호가.class, SectorF_호가Feed.class),
    G7094("G7094".getBytes(), TRDescription.변동성지수선물, SECTOR_체결호가.class, SectorF_체결호가Feed.class),

    // Kosdaq 선물
    A3024("A3024".getBytes(), TRDescription.코스닥150선물, KQ_체결.class, KQFutures_체결Feed.class),
    B6024("B6024".getBytes(), TRDescription.코스닥150선물, KQ_호가.class, KQFutures_호가Feed.class),
    G7024("G7024".getBytes(), TRDescription.코스닥150선물, KQ_체결호가.class, KQFutures_체결호가Feed.class),

    // 상품 선물
    A3016("A3016".getBytes(), TRDescription.상품선물, CmtyFuture_체결.class, CmtyFuture_체결Feed.class),
    B6016("B6016".getBytes(), TRDescription.상품선물, CmtyFuture_호가.class, CmtyFuture_호가Feed.class),
    G7016("G7016".getBytes(), TRDescription.상품선물, CmtyFuture_체결호가.class, CmtyFuture_체결호가Feed.class),

    A3034("A3034".getBytes(), TRDescription.K200옵션, Option_체결.class, Option_체결Feed.class),
    B6034("B6034".getBytes(), TRDescription.K200옵션, Option_호가.class, Option_호가Feed.class),
    G7034("G7034".getBytes(), TRDescription.K200옵션, Option_체결호가.class, Option_체결호가Feed.class),
//    B2034("B2034".getBytes(), TRDescription.K200옵션_시세Recovery, Option_Recovery.class, Option_RecoveryFeed.class),

    // 위클리옵션
    A3184("A3184".getBytes(), TRDescription.K200위클리옵션, Option_체결.class, Option_체결Feed.class),
    B6184("B6184".getBytes(), TRDescription.K200위클리옵션, Option_호가.class, Option_호가Feed.class),
    G7184("G7184".getBytes(), TRDescription.K200위클리옵션, Option_체결호가.class, Option_체결호가Feed.class),

    // 미니옵션
    A3134("A3134".getBytes(), TRDescription.미니K200옵션, Option_체결.class, Option_체결Feed.class),
    B6134("B6134".getBytes(), TRDescription.미니K200옵션, Option_호가.class, Option_호가Feed.class),
    G7134("G7134".getBytes(), TRDescription.미니K200옵션, Option_체결호가.class, Option_체결호가Feed.class),

    A3011("A3011".getBytes(), TRDescription.코스피, Equity_체결.class, Equity_체결Feed.class),
    B6011("B6011".getBytes(), TRDescription.코스피, Equity_호가.class, Equity_호가Feed.class),

    A3012("A3012".getBytes(), TRDescription.코스닥, Equity_체결.class, Equity_체결Feed.class),
    B6012("B6012".getBytes(), TRDescription.코스닥, Equity_호가.class, Equity_호가Feed.class),

    A3015("A3015".getBytes(), TRDescription.주식선물, SF_체결.class, SF_체결Feed.class),
    B6015("B6015".getBytes(), TRDescription.주식선물, SF_호가.class, SF_호가Feed.class),
    G7015("G7015".getBytes(), TRDescription.주식선물, SF_체결호가.class, SF_체결호가Feed.class),
    V1015("V1015".getBytes(), TRDescription.주식선물, SF_가격제한폭확대.class, SF_가격제한폭확대Feed.class),

    A3025("A3025".getBytes(), TRDescription.주식옵션, SO_체결.class, SO_체결Feed.class),
    B6025("B6025".getBytes(), TRDescription.주식옵션, SO_호가.class, SO_호가Feed.class),
    G7025("G7025".getBytes(), TRDescription.주식옵션, SO_체결호가.class, SO_체결호가Feed.class),

    A3174("A3174".getBytes(), TRDescription.코스닥150옵션, KQOption_체결.class, KQOption_체결Feed.class),
    B6174("B6174".getBytes(), TRDescription.코스닥150옵션, KQOption_호가.class, KQOption_호가Feed.class),
    G7174("G7174".getBytes(), TRDescription.코스닥150옵션, KQOption_체결호가.class, KQOption_체결호가Feed.class),

    B7011("B7011".getBytes(), TRDescription.코스피, ETF_ELW_호가.class, ETF_호가Feed.class),
    F7011("F7011".getBytes(), TRDescription.ETF, ETF_NAV.class, ETF_NAVFeed.class),
    BV011("BV011".getBytes(), TRDescription.ETF, ETF_NAV.class, ETF_NAVFeed.class),
    @Hint(info = "for Kosdaq can not find parse info for F7012")
//    F7012("F7012".getBytes(), TRDescription.NAV, ETF_NAV.class, UndefinedFeed.class) ,
    L5011("L5011".getBytes(), TRDescription.해외지수ETF, ETF_NAV.class, ETF_NAVFeed.class),
    F8011("F8011".getBytes(), TRDescription.종목배치, ETF_PDF.class, ETF_PDFFeed.class),
    //@Hint(info = "for Kosdaq")
    //F8012("F8012".getBytes(), TRDescription.PDF, ETF_PDF.class, ETF_PDFFeed.class) ,
    A3021("A3021".getBytes(), TRDescription.ELW, Equity_체결.class, Equity_체결Feed.class),
    B7021("B7021".getBytes(), TRDescription.ELW, ETF_ELW_호가.class, ETF_호가Feed.class),

    // 가격제한폭 확대 발동
    V1014("V1014".getBytes(), TRDescription.K200선물, K200선물_가격제한폭확대.class, K200선물_가격제한폭확대Feed.class),  // 원지수

    V1024("V1024".getBytes(), TRDescription.코스닥150선물, KQ150선물_가격제한폭확대.class, KQ150선물_가격제한폭확대Feed.class),
    V1025("V1025".getBytes(), TRDescription.주식옵션, SO_가격제한폭확대.class, SO_가격제한폭확대Feed.class),
    V1034("V1034".getBytes(), TRDescription.K200옵션, K200옵션_가격제한폭확대.class, K200옵션_가격제한폭확대Feed.class),  // 원지수
    V1104("V1104".getBytes(), TRDescription.섹터지수선물, SectorF_가격제한폭확대.class, SectorF_가격제한폭확대Feed.class),
    V1124("V1124".getBytes(), TRDescription.미니K200선물, K200선물_가격제한폭확대.class, K200선물_가격제한폭확대Feed.class),  // 미니
    V1134("V1134".getBytes(), TRDescription.미니K200옵션, K200옵션_가격제한폭확대.class, K200옵션_가격제한폭확대Feed.class),  // 미니
    V1184("V1134".getBytes(), TRDescription.K200위클리옵션, K200옵션_가격제한폭확대.class, K200옵션_가격제한폭확대Feed.class),  // 위클리
    V1164("V1164".getBytes(), TRDescription.코스닥150선물, KQ150선물_가격제한폭확대.class, KQ150선물_가격제한폭확대Feed.class),    // KRX300
    V1174("V1174".getBytes(), TRDescription.코스닥150옵션, KQ150옵션_가격제한폭확대.class, KQ150옵션_가격제한폭확대Feed.class),

    // 실시간 상하한가
//    Q2016("Q2016".getBytes(), TRDescription.extendPriceLimit, CmtyFuture_실시간상하한가.class, CmtyFuture_실시간상하한가Feed.class),

    /**
     * 종가
     */
    A6014("A6014".getBytes(), TRDescription.K200선물_종가, Future_종가.class, Future_종가Feed.class),
    A6124("A6124".getBytes(), TRDescription.미니K200선물_종가, Future_종가.class, Future_종가Feed.class), // 미니 선물 종목마감
    A6034("A6034".getBytes(), TRDescription.K200옵션_종가, Option_종가.class, Option_종가Feed.class),
    A6184("A6184".getBytes(), TRDescription.K200위클리옵션_종가, Option_종가.class, Option_종가Feed.class), //위클리 옵션 종목 마감
    A6134("A6134".getBytes(), TRDescription.미니K200옵션_종가, Option_종가.class, Option_종가Feed.class), // 미니 옵션 종목마감
    A6174("A6174".getBytes(), TRDescription.코스닥150옵션_종가, KQOption_종가.class, KQOption_종가Feed.class), // KQ150 옵션 종목마감
    A6164("A6164".getBytes(), TRDescription.KRX300선물_종가, KRX300Futures_종가.class, KRX300Futures_종가Feed.class), // KRX300 선물 종목마감
    A6024("A6024".getBytes(), TRDescription.코스닥150선물_종가, KQFutures_종가.class, KQFutures_종가Feed.class), // 코스닥 선물 종목마감
    A6104("A6104".getBytes(), TRDescription.섹터지수선물_종가, SectorF_종가.class, SectorF_종가Feed.class), // 섹터 선물 마감
    A6094("A6094".getBytes(), TRDescription.변동성지수선물_종가, SectorF_종가.class, SectorF_종가Feed.class), // 변동성지수 선물 마감
    A6015("A6015".getBytes(), TRDescription.주식선물_종가, SF_종가.class, SF_종가Feed.class), // 주식 선물 마감
    A6025("A6025".getBytes(), TRDescription.주식옵션_종가, SO_종가.class, SO_종가Feed.class), // 주식 옵션 마감
    A6016("A6016".getBytes(), TRDescription.상품선물_종가, CmtyFuture_종가.class, CmtyFuture_종가Feed.class), // 상품 선물 마감
    A6011("A6011".getBytes(), TRDescription.코스피_종가, Equity_종가.class, Equity_종가Feed.class),
    A6021("A6021".getBytes(), TRDescription.ELW_종가, Equity_종가.class, Equity_종가Feed.class),
    A6012("A6012".getBytes(), TRDescription.코스닥_종가, Equity_종가.class, Equity_종가Feed.class),

    /**
     * 시세종가 - 시고종저 포함
     */
    B1011("B1011".getBytes(), TRDescription.코스피_종가, Equity_시세종가.class, Equity_시세종가Feed.class),
    B1021("B1021".getBytes(), TRDescription.ELW_종가, Equity_시세종가.class, Equity_시세종가Feed.class),
    B1012("B1012".getBytes(), TRDescription.코스닥_종가, Equity_시세종가.class, Equity_시세종가Feed.class),
    C1011("C1011".getBytes(), TRDescription.코스피_통계, 시장통계_현물_종목별투자자별_종가.class, 시장통계_현물_종목별투자자별_종가Feed.class),
    C1012("C1012".getBytes(), TRDescription.코스닥_통계, 시장통계_현물_종목별투자자별_종가.class, 시장통계_현물_종목별투자자별_종가Feed.class),
    C1021("C1021".getBytes(), TRDescription.ELW_통계, 시장통계_현물_종목별투자자별_종가.class, 시장통계_현물_종목별투자자별_종가Feed.class),

    /**
     * 지수
     * D2 : data 구분 , 01 : 주식 , 1: 유가증권 , 2: 코스닥
     */
    D2011("D2011".getBytes(), TRDescription.지수, 지수.class, 지수_Feed.class), // Kospi 200 지수
    T9012("T9012".getBytes(), TRDescription.지수, 지수.class, 지수_Feed.class), // 코스닥 150 지수
    N5011("N5011".getBytes(), TRDescription.지수, 지수.class, 지수_Feed.class), // K200 섹터 지수
    AA011("AA011".getBytes(), TRDescription.지수, 지수.class, 지수_Feed.class), // KRX300 섹터 지수
    S6011("S6011".getBytes(), TRDescription.지수, 지수.class, 지수_Feed.class), // 코스피 배당성장 50
    S4011("S4011".getBytes(), TRDescription.지수, 지수.class, 지수_Feed.class), // 코스피 고배당 50
    J3034("J3034".getBytes(), TRDescription.지수, 지수.class, 지수_Feed.class), // 변동성 지수
    AG011("AG011".getBytes(), TRDescription.지수, 지수.class, 지수_Feed.class), // KRX Mid 200 지수 (+ K-뉴딜)

    D3011("D3011".getBytes(), TRDescription.지수, 지수.class, 지수_예상_Feed.class), // Kospi 200 예상지수
    U4012("U4012".getBytes(), TRDescription.지수, 지수.class, 지수_예상_Feed.class), // 코스닥 150 예상지수
    N6011("N6011".getBytes(), TRDescription.지수, 지수.class, 지수_예상_Feed.class), // K200 섹터 예상지수
    AB011("AB011".getBytes(), TRDescription.지수, 지수.class, 지수_예상_Feed.class), // KRX300 섹터 예상지수
    V2011("V2011".getBytes(), TRDescription.지수, 지수.class, 지수_예상_Feed.class), // 코스피 배당성장 50 예상지수
    V0011("V0011".getBytes(), TRDescription.지수, 지수.class, 지수_예상_Feed.class), // 코스피 고배당 50 예상지수
    AH011("AH011".getBytes(), TRDescription.지수, 지수.class, 지수_예상_Feed.class), // KRX Mid 200 예상지수 (+ K-뉴딜 예상)

    //옵션 민감도
    N7034("N7034".getBytes(), TRDescription.K200옵션_민감도, Option_민감도.class, Option_민감도Feed.class),
    N7134("N7134".getBytes(), TRDescription.미니K200옵션_민감도, Option_민감도.class, Option_민감도Feed.class),
    N7174("N7174".getBytes(), TRDescription.코스닥150옵션_민감도, Option_민감도.class, Option_민감도Feed.class),
    N7025("N7025".getBytes(), TRDescription.주식옵션_민감도, Option_민감도.class, Option_민감도Feed.class),


    C6021("C6021".getBytes(), TRDescription.ELW_통계, NeedToImplement.class, UndefinedFeed.class),
    C7021("C7021".getBytes(), TRDescription.ELW_민감도, NeedToImplement.class, UndefinedFeed.class),

    /**
     * 시장통계
     */
    P0011("P0011".getBytes(), TRDescription.코스피_통계, 시장통계_프로그램매매투자자별.class, 시장통계_프로그램매매투자자별Feed.class), // 유가증권 프로그램매매매 투자자별
    P0012("P0012".getBytes(), TRDescription.코스닥_통계, 시장통계_프로그램매매투자자별.class, 시장통계_프로그램매매투자자별Feed.class), // 코스닥 프로그램매매매 투자자별

    //mktStatFutTradeByInvestor, //H1014  H1015  H1104 H1024 H1124
    H1014("H1014".getBytes(), TRDescription.K200선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), //
    H1015("H1015".getBytes(), TRDescription.주식선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), //
    H1104("H1104".getBytes(), TRDescription.섹터지수선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), //
    H1024("H1024".getBytes(), TRDescription.코스닥150선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), //
    H1124("H1124".getBytes(), TRDescription.미니K200선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), //
    H1164("H1164".getBytes(), TRDescription.KRX300선물_통계, 시장통계_선물_투자자데이터.class, 시장통계_선물_투자자데이터Feed.class), //KRX300

    //mktStatOptTradeByInvestor, //H1034  H1025  H1134 H1174
    H1034("H1034".getBytes(), TRDescription.K200옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //
    H1184("H1184".getBytes(), TRDescription.K200위클리옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //
    H1025("H1025".getBytes(), TRDescription.주식옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //
    H1134("H1134".getBytes(), TRDescription.미니K200옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //
    H1174("H1174".getBytes(), TRDescription.코스닥150옵션_통계, 시장통계_옵션_투자자데이터.class, 시장통계_옵션_투자자데이터Feed.class), //

    B9011("B9011".getBytes(), TRDescription.코스피_통계, 시장통계_거래원.class, 시장통계_거래원Feed.class), // 코스피 거래원
    B9021("B9021".getBytes(), TRDescription.ELW_통계, 시장통계_거래원.class, 시장통계_거래원Feed.class), // ELW 거래원
    B9012("B9012".getBytes(), TRDescription.코스닥_통계, 시장통계_거래원.class, 시장통계_거래원Feed.class), // 코스닥 거래원

    /**
     * 종목정보 - Batch
     */
    A0034("A0034".getBytes(), TRDescription.종목배치, Option_종목정보.class, Option_종목정보Feed.class), // K200 옵션
    A0184("A0184".getBytes(), TRDescription.종목배치, Option_종목정보.class, Option_종목정보Feed.class), // 위클리 옵션
    A0134("A0134".getBytes(), TRDescription.종목배치, Option_종목정보.class, Option_종목정보Feed.class), // 미니
    A0015("A0015".getBytes(), TRDescription.종목배치, 선물_종목정보.class, SF_종목정보Feed.class), // 주식선물
    A0016("A0016".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 상품선물_종목정보Feed.class), // 상품선물
    A0014("A0014".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // K200 선물
    A0124("A0124".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // 미니 선물
    A0104("A0104".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // 섹터 선물
    A0024("A0024".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // 코스닥 선물
    A0011("A0011".getBytes(), TRDescription.종목배치, Equity_종목정보.class, Equity_종목정보Feed.class), // 유가증권
    A0012("A0012".getBytes(), TRDescription.종목배치, Equity_종목정보.class, Equity_종목정보Feed.class), // 코스닥
    A0144("A0144".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // 유로스탁스50 선물
    A0025("A0025".getBytes(), TRDescription.종목배치, Option_종목정보.class, SO_종목정보Feed.class), // 주식옵션
    A0164("A0164".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // KRX300 선물
    A0174("A0174".getBytes(), TRDescription.종목배치, Option_종목정보.class, Option_종목정보Feed.class), // KQ150옵션
    A0094("A0094".getBytes(), TRDescription.종목배치, 선물_종목정보.class, 선물_종목정보Feed.class), // vkospi 선물

    A1011("A1011".getBytes(), TRDescription.종목배치, ELW_종목정보.class, ELW_종목정보Feed.class), // ELW 부가
    N8011("N8011".getBytes(), TRDescription.종목배치, ETF_사무수탁배치.class, ETF_사무수탁배치Feed.class), // etf 배치 (cu 수량)


    // 부산 K200 선물
    P_A3014("A3014".getBytes(), TRDescription.부산_K200선물, PusanFuture_체결.class, PusanFuture_체결Feed.class),
    P_B6014("B6014".getBytes(), TRDescription.부산_K200선물, PusanFuture_호가.class, PusanFuture_호가Feed.class),
    P_G7014("G7014".getBytes(), TRDescription.부산_K200선물, PusanFuture_체결호가.class, PusanFuture_체결호가Feed.class),

    // 부산 미니 선물
    P_A3124("A3124".getBytes(), TRDescription.부산_미니K200선물, PusanFuture_체결.class, PusanFuture_체결Feed.class),
    P_B6124("B6124".getBytes(), TRDescription.부산_미니K200선물, PusanFuture_호가.class, PusanFuture_호가Feed.class),
    P_G7124("G7124".getBytes(), TRDescription.부산_미니K200선물, PusanFuture_체결호가.class, PusanFuture_체결호가Feed.class),

    // 부산 K200 옵션
    P_A3034("A3034".getBytes(), TRDescription.부산_K200옵션, PusanOption_체결.class, PusanOption_체결Feed.class),
    P_B6034("B6034".getBytes(), TRDescription.부산_K200옵션, PusanOption_호가.class, PusanOption_호가Feed.class),
    P_G7034("G7034".getBytes(), TRDescription.부산_K200옵션, PusanOption_체결호가.class, PusanOption_체결호가Feed.class),

    // 부산 위클리 옵션
    P_A3184("A3184".getBytes(), TRDescription.부산_K200위클리옵션, PusanOption_체결.class, PusanOption_체결Feed.class),
    P_B6184("B6184".getBytes(), TRDescription.부산_K200위클리옵션, PusanOption_호가.class, PusanOption_호가Feed.class),
    P_G7184("G7184".getBytes(), TRDescription.부산_K200위클리옵션, PusanOption_체결호가.class, PusanOption_체결호가Feed.class),

    // 부산 미니옵션
    P_A3134("A3134".getBytes(), TRDescription.부산_미니K200옵션, PusanOption_체결.class, PusanOption_체결Feed.class),
    P_B6134("B6134".getBytes(), TRDescription.부산_미니K200옵션, PusanOption_호가.class, PusanOption_호가Feed.class),
    P_G7134("G7134".getBytes(), TRDescription.부산_미니K200옵션, PusanOption_체결호가.class, PusanOption_체결호가Feed.class),

    // 부산 가격제한폭 확대 발동
    P_V1015("V1014".getBytes(), TRDescription.부산_K200선물, PusanK200선물_가격제한폭확대.class, PusanK200선물_가격제한폭확대Feed.class), // 원지수
    P_V1124("V1124".getBytes(), TRDescription.부산_미니K200선물, PusanK200선물_가격제한폭확대.class, PusanK200선물_가격제한폭확대Feed.class),  // 미니
    P_V1034("V1034".getBytes(), TRDescription.부산_K200옵션, PusanK200옵션_가격제한폭확대.class, PusanK200옵션_가격제한폭확대Feed.class),  // 원지수
    P_V1134("V1134".getBytes(), TRDescription.부산_미니K200옵션, PusanK200옵션_가격제한폭확대.class, PusanK200옵션_가격제한폭확대Feed.class),  // 미니

    // 부산 실시간 상하한가
    // Q2016 PusanTrInfoList에 추가 필요
//    P_Q2016("Q2016".getBytes(), TRDescription.extendPriceLimit, PusanCmtyFuture_실시간상하한가.class, PusanCmtyFuture_실시간상하한가Feed.class),

    // 유로스탁스50 선물
    A3144("A3144".getBytes(), TRDescription.유로스톡스50선물, EuroStoxx50_체결.class, EuroStoxx50_체결Feed.class),
    B6144("B6144".getBytes(), TRDescription.유로스톡스50선물, EuroStoxx50_호가.class, EuroStoxx50_호가Feed.class),
    G7144("G7144".getBytes(), TRDescription.유로스톡스50선물, EuroStoxx50_체결호가.class, EuroStoxx50_체결호가Feed.class),

    //공시
    F0011("F0011".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class),
    F0012("F0012".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class),
    F0018("F0018".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class),
    F0909("F0909".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class),
    E9011("E9011".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class),
    E9012("E9012".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class),
    E9018("E9018".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class),
    E9909("E9909".getBytes(), TRDescription.공시, 공시.class, 공시_Feed.class),

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

    @Override
    public byte[] getTrBytes() {
        return trBytes;
    }

    @Override
    public Constructor<? extends Feed> getConstructor() {
        return constructor;
    }

    @Override
    public Class<? extends FeedParser> getParser() {
        return parser;
    }

    @Override
    public String getTrCodeStr() {
        return trCodeStr;
    }

    @Override
    public TRDescription getTrDescription() {
        return description;
    }
}

