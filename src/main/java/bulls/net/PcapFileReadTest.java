package bulls.net;

import bulls.designTemplate.GeneralFileReader;
import bulls.feed.file.Protocol;
import bulls.feed.file.UniversalPcapFileReader;
import bulls.feed.next.parser.equity.Equity_종목정보;
import bulls.feed.test.FeedParserChecker;

public class PcapFileReadTest {

    public static void main(String[] args) {
        String filename = "dump.pcap";
        GeneralFileReader reader = new UniversalPcapFileReader(filename, Protocol.UDP);
        if (!reader.init()) {
            System.out.println("UniversalPcapFileReader init error");
            return;
        }

        int timeLength = 15;

        FeedParserChecker checker = new FeedParserChecker(false);

        // next 테스트
//        checker.add("C101S", 종목별_투자자별_종가통계.class);  // 아예 안됨
//        checker.add("C101Q", 종목별_투자자별_종가통계.class);
//        checker.add("C102S", 종목별_투자자별_종가통계.class);
//        checker.add("C103S", 종목별_투자자별_종가통계.class);
//        checker.add("C104S", 종목별_투자자별_종가통계.class);

//        checker.add("IA000", 지수.class);
//        checker.add("IB000", 지수.class);

//        checker.add("N703F", Option_민감도.class);  // OK
//        checker.add("N712F", Option_민감도.class);  // OK
//        checker.add("N715F", Option_민감도.class);  // 아예 안들어옴
//        checker.add("N705F", Option_민감도.class);  // 아예 안들어옴


//        checker.add("C602S", NeedToImplement.class);  // 정의된 파싱정보 없음
//        checker.add("C7021", NeedToImplement.class);  // 정의된 파싱정보 없음

//        checker.add("P001S", 시장통계_프로그램매매투자자별.class);  // OK
//        checker.add("P001Q", 시장통계_프로그램매매투자자별.class);  // OK
//
//        checker.add("H101F", 시장통계_선물_투자자데이터.class);  // OK
//        checker.add("H104F", 시장통계_선물_투자자데이터.class);  // OK
//        checker.add("H109F", 시장통계_선물_투자자데이터.class);  // OK
//        checker.add("H102F", 시장통계_선물_투자자데이터.class);  // OK
//        checker.add("H111F", 시장통계_선물_투자자데이터.class);  // OK
//        checker.add("H113F", 시장통계_선물_투자자데이터.class);  // OK
//
//        checker.add("H103F", 시장통계_옵션_투자자데이터.class);  // OK
//        checker.add("H116F", 시장통계_옵션_투자자데이터.class);  // OK
//        checker.add("H105F", 시장통계_옵션_투자자데이터.class);  // OK
//        checker.add("H112F", 시장통계_옵션_투자자데이터.class);  // OK
//        checker.add("H115F", 시장통계_옵션_투자자데이터.class);  // OK
//        checker.add("B9011", 시장통계_거래원.class);    // TR_CODE 없음
//        checker.add("B9021", 시장통계_거래원.class);    // TR_CODE 없음
//        checker.add("B9012", 시장통계_거래원.class);    // TR_CODE 없음
//        checker.add("A003F", Option_종목정보.class);  // OK
//        checker.add("A016F", Option_종목정보.class);  // OK
//        checker.add("A012F", Option_종목정보.class);  // OK
//        checker.add("A005F", Option_종목정보.class);  // OK
//        checker.add("A015F", Option_종목정보.class);  // OK
//        checker.add("A004F", 선물_종목정보.class);     // OK
//        checker.add("A006F", 선물_종목정보.class);     // OK
//        checker.add("A010F", 선물_종목정보.class);     // OK
//        checker.add("A001F", 선물_종목정보.class);     // OK
//        checker.add("A011F", 선물_종목정보.class);     // OK
//        checker.add("A009F", 선물_종목정보.class);     // OK
//        checker.add("A002F", 선물_종목정보.class);     // OK
//        checker.add("A014F", 선물_종목정보.class);     // OK
//        checker.add("A013F", 선물_종목정보.class);     // OK
//        checker.add("A008F", 선물_종목정보.class);     // OK
        checker.add("A001S", Equity_종목정보.class);
        checker.add("A002S", Equity_종목정보.class);
        checker.add("A003S", Equity_종목정보.class);
        checker.add("A004S", Equity_종목정보.class);
        checker.add("A001Q", Equity_종목정보.class);
//        checker.add("A102S", ELW_종목정보.class);
//        checker.add("N8011", ETF_사무수탁배치.class);
//        checker.add("F000S", 공시.class);
//        checker.add("F000Q", 공시.class);
//        checker.add("F000X", 공시.class);
//        checker.add("F0909", 공시.class);
//        checker.add("E900S", 공시.class);
//        checker.add("E900Q", 공시.class);
//        checker.add("E900X", 공시.class);
//        checker.add("E9909", 공시.class);


        // current 테스트
//        checker.add(FeedTRCode.A0011);


        while (true) {
            String line = reader.next();
            if (line == null)
                break;

            String timeString = line.substring(0, timeLength);
            String packetString = line.substring(timeLength);

            checker.check(packetString);
//            System.out.println("time : " + timeString + ", packet : " + packetString);
        }

        reader.close();
    }
}
