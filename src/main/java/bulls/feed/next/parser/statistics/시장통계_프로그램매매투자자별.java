package bulls.feed.next.parser.statistics;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 시장통계_프로그램매매투자자별 implements FeedParser {
    /////////
    //    투자자코드	명칭		비고
    //1000	금융투자업자		구:증권회사 및 선물회사
    //1500	삭제(1000번으로 통합)		구:선물회사
    //2000	보험회사
    //3000	자산운용회사 및 투자회사
    //3100	사모펀드
    //4000	은행		자산운용회사의 신탁재산은 자산운용회사로 분류
    //5000	기타금융기관		구:종합금융회사, 상호저축은행
    //6000	연금, 기금 및 공제회
    //6100	삭제		구:기타금융기관
    //7000	국가/ 지방자치단체,국제기구 및 공익기관		구:국가, 지방자치단체 및 국제기구
    //7100	기타법인		기존 7000번 분류를 세분화하여 추가확정
    //8000	개인
    //9000	외국인투자등록ID가 있는 외국인		거주, 비거주
    //9001	외국인투자등록ID가 없는 외국인		거주, 비거주
    //
    //"※주의사항 : 장외채권의 경우에만 아직 구 투자자 코드체계인 1000:증권회사, 1500:선물회사 방식임"

    trCode(0, 5, PacketType.String), // P0 / 01:주식 /1:유가증권 2:코스닥
    시장구분(4, 1, PacketType.String), //1:유가증권 2:코스닥
    산출시각(5, 6, PacketType.String),  //통계산출시각(5, 6, PacketType.String),
    투자자구분코드(11, 4, PacketType.String),  //투자자구분코드(11, 4, PacketType.String),
    매도차익체결수량(15, 15, PacketType.Integer),  //매도차익체결수량(15, 15, PacketType.Integer),
    매도차익거래대금(30, 22, PacketType.Float),  //매도차익거래대금(30, 22, PacketType.Float),
    매도비차익체결수량(52, 15, PacketType.Integer),  //매도비차익체결수량(52, 15, PacketType.Integer),
    매도비차익거래대금(67, 22, PacketType.Float),  //매도비차익거래대금(67, 22, PacketType.Float),
    매수차익체결수량(89, 15, PacketType.Integer),   //매수차익체결수량(89, 15, PacketType.Integer),
    매수차익거래대금(104, 22, PacketType.Float),   //매수차익거래대금(104, 22, PacketType.Float),
    매수비차익체결수량(126, 15, PacketType.Integer),  //매수비차익체결수량(126, 15, PacketType.Integer),
    매수비차익거래대금(141, 22, PacketType.Float);  //매수비차익거래대금(141, 22, PacketType.Float),

    시장통계_프로그램매매투자자별(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
