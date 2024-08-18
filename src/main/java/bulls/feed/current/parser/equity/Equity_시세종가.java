package bulls.feed.current.parser.equity;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

/**
 * 주식, ELW, ETF 포함
 */
public enum Equity_시세종가 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    DATA구분(0, 2, PacketType.String),//B1:종가 B2:시세REC
    정보구분(2, 2, PacketType.String),//01:주식 02:ELW
    시장구분(4, 1, PacketType.String),//1:유가증권 2:코스닥
    종목코드(5, 12, PacketType.String),//표준코드
    종목일련번호(17, 5, PacketType.Integer),//종목배치(A0)의 종목일련번호
    등락구분(22, 1, PacketType.String),//0:판단불가 1:상한 2:상승 3:보합 4:하한 5:하락
    전일대비(23, 9, PacketType.Integer),//단위:원
    현재가(32, 9, PacketType.Integer),//
    시가(41, 9, PacketType.Integer),//정규장 시가
    고가(50, 9, PacketType.Integer),//정규장 고가
    저가(59, 9, PacketType.Integer),//정규장 저가
    매도호가(68, 9, PacketType.Integer),//B1시 0값 SET
    매수호가(77, 9, PacketType.Integer),//B1시 0값 SET
    누적체결수량(86, 12, PacketType.Integer),//단위:주 ※시간외단일가, BUY-IN을 제외한 모든 체결수량 합산
    누적거래대금(98, 18, PacketType.Integer),//단위:원 ※시간외단일가, BUY-IN을 제외한 모든 거래대금 합산
    실세_기세_구분(116, 1, PacketType.String),//0:초기값 1:실세 2:기세 3:거래무 4:시가기준가종목의 기세
    보드이벤트ID(117, 3, PacketType.String),//B1/B2 에서는 보드이벤트ID 사용금지(과거 상태값이 전송될 수 있음). 추후, 해당 항목 '000' 세팅 예정. 2014.03.24)
    보드ID(120, 2, PacketType.String),//※ 코드값모음 참조
    거래정지여부(122, 1, PacketType.String),//Y, N
    장개시전시간외종가_체결수량(123, 12, PacketType.Integer),//대량, 바스켓은 제외, 단위:주
    장개시전시간외종가_거래대금(135, 18, PacketType.Integer),//대량, 바스켓은 제외, 단위:원
    정규장체결수량(153, 12, PacketType.Integer),//대량, 바스켓은 제외, 단위:주
    정규장거래대금(165, 18, PacketType.Integer),//대량, 바스켓은 제외, 단위:원
    장종료후시간외종가_체결수량(183, 12, PacketType.Integer),//대량, 바스켓은 제외, 단위:주
    장종료후시간외종가_거래대금(195, 18, PacketType.Integer),//대량, 바스켓은 제외, 단위:원
    ELW조기종료여부(213, 1, PacketType.String),//Y,N 조기종료ELW 外 종목은 Default 'N' SET 2010.08.30
    ELW조기종료시간(214, 6, PacketType.Integer),//HHMMSS, ELW조기종료여부가 'Y'인 경우만 설정
    경쟁대량_방향구분(220, 1, PacketType.Integer),//0:해당없음, 1:매도, 2:매수
    일반_Buy_in_체결수량(221, 12, PacketType.Integer),//단위:주
    일반_Buy_in_거래대금(233, 18, PacketType.Integer),//단위:원
    당일_Buy_in_체결수량(251, 12, PacketType.Integer),//단위:주
    당일_Buy_in_거래대금(263, 18, PacketType.Integer),//단위:원
    FILLER(281, 8, PacketType.String),//SPACE
    FF(289, 1, PacketType.String);//END OF TEXT (%HFF)


    Equity_시세종가(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

}
