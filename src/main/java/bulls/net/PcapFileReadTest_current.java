package bulls.net;

import bulls.designTemplate.GeneralFileReader;
import bulls.feed.file.Protocol;
import bulls.feed.file.UniversalPcapFileReader;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.test.FeedParserChecker;

public class PcapFileReadTest_current {

    public static void main(String[] args) {
        String filename = "dump.pcap";
        GeneralFileReader reader = new UniversalPcapFileReader(filename, Protocol.UDP);
        if (!reader.init()) {
            System.out.println("UniversalPcapFileReader init error");
            return;
        }

        int timeLength = 15;

        FeedParserChecker checker = new FeedParserChecker(false);

        // current 테스트
//        // 시장통계_현물_종목별투자자별_종가
//        checker.add(FeedTRCode.C1011);
//        checker.add(FeedTRCode.C1012);
//        checker.add(FeedTRCode.C1021);
//        //지수
//        checker.add(FeedTRCode.D2011);
//        checker.add(FeedTRCode.T9012);
//        checker.add(FeedTRCode.N5011);
//        checker.add(FeedTRCode.AA011);
//        checker.add(FeedTRCode.S6011);
//        checker.add(FeedTRCode.S4011);
//        checker.add(FeedTRCode.J3034);
//        checker.add(FeedTRCode.AG011);
//        checker.add(FeedTRCode.D3011);
//        checker.add(FeedTRCode.U4012);
//        checker.add(FeedTRCode.N6011);
//        checker.add(FeedTRCode.AB011);
//        checker.add(FeedTRCode.V2011);
//        checker.add(FeedTRCode.V0011);
//        checker.add(FeedTRCode.AH011);
//        //옵션 민감도
//        checker.add(FeedTRCode.N7034);   // OK
//        checker.add(FeedTRCode.N7134);   // OK
//        checker.add(FeedTRCode.N7174);   // OK
//        checker.add(FeedTRCode.N7025);   // OK
//
//        //NeedToImplement
//        checker.add(FeedTRCode.C6021);
//        checker.add(FeedTRCode.C7021);
//        //시장통계_프로그램매매투자자별
//        checker.add(FeedTRCode.P0011);
//        checker.add(FeedTRCode.P0012);
//        //시장통계_선물_투자자데이터
//        checker.add(FeedTRCode.H1014);
//        checker.add(FeedTRCode.H1015);
//        checker.add(FeedTRCode.H1104);
//        checker.add(FeedTRCode.H1024);
//        checker.add(FeedTRCode.H1124);
//        checker.add(FeedTRCode.H1164);
//        //시장통계_옵션_투자자데이터
//        checker.add(FeedTRCode.H1034);
//        checker.add(FeedTRCode.H1184);
//        checker.add(FeedTRCode.H1025);
//        checker.add(FeedTRCode.H1134);
//        checker.add(FeedTRCode.H1174);
//        // 시장거래원
//        checker.add(FeedTRCode.B9011);
//        checker.add(FeedTRCode.B9021);
//        checker.add(FeedTRCode.B9012);
//        // 종목정보
//        checker.add(FeedTRCode.A0034);  // 옵션
//        checker.add(FeedTRCode.A0184);
//        checker.add(FeedTRCode.A0134);
//        checker.add(FeedTRCode.A0015);  // 선물
//        checker.add(FeedTRCode.A0016);
//        checker.add(FeedTRCode.A0014);
//        checker.add(FeedTRCode.A0124);
//        checker.add(FeedTRCode.A0104);
//        checker.add(FeedTRCode.A0024);
//        checker.add(FeedTRCode.A0011);
        checker.add(FeedTRCode.A0012);  // 에쿼티
//        checker.add(FeedTRCode.A0144);
//        checker.add(FeedTRCode.A0025);
//        checker.add(FeedTRCode.A0164);
//        checker.add(FeedTRCode.A0174);
//        checker.add(FeedTRCode.A0094);
//        // ELW, ETF
//        checker.add(FeedTRCode.A1011);
//        checker.add(FeedTRCode.N8011);
//        // 공시
//        checker.add(FeedTRCode.F0011);
//        checker.add(FeedTRCode.F0012);
//        checker.add(FeedTRCode.F0018);
//        checker.add(FeedTRCode.F0909);
//        checker.add(FeedTRCode.E9011);
//        checker.add(FeedTRCode.E9012);
//        checker.add(FeedTRCode.E9018);
//        checker.add(FeedTRCode.E9909);


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
