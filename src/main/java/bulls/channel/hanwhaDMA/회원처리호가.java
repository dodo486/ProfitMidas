package bulls.channel.hanwhaDMA;

import bulls.packet.OffsetLength;
import bulls.packet.PacketType;
public enum 회원처리호가 {

    HeaderBodyLength(0, 4, PacketType.Integer),
    HeaderMsgType(4, 1, PacketType.String),
    HeaderResponseCode(5, 4, PacketType.String),
    HeaderMsgSeqNum(9, 10, PacketType.Integer),
    HeaderMsgCount(19, 1, PacketType.Integer),
    TR구분(20, 1, PacketType.String),
    주문번호(21, 10, PacketType.String),
    원주문번호(31, 10, PacketType.String),
    종목코드(41, 12, PacketType.String),
    _9자리코드(43, 9, PacketType.String),
    매도매수구분코드(53, 1, PacketType.String),
    정정취소구분코드(54, 1, PacketType.String),
    계좌번호(55, 12, PacketType.String),
    호가수량(67, 10, PacketType.Integer),
    호가가격(77, 11, PacketType.Float),
    시장가지정가(88, 1, PacketType.String),
    일반IOCFOK(89, 1, PacketType.String),
    실정정취소호가수량(90, 10, PacketType.Integer),
    자동취소타입(100, 1, PacketType.String),
    거부코드(101, 4, PacketType.String),
    매도유형코드(105, 2, PacketType.String),
    PT구분코드(107, 2, PacketType.String),
    사용자영역(109, 20, PacketType.String),
    북코드(109, 14, PacketType.String),
    FILLER1(123, 3, PacketType.String),
    Purpose(126, 1, PacketType.String),
    매도잔고유형(127, 1, PacketType.String), // 0:일반잔고 1:대차잔고 2:권리잔고 2021.07.08 미니원장 소스 추가
    FepOutputFileId(128, 1, PacketType.String),
    filler(129, 12, PacketType.String), // 129번째에 알 수 없는 데이터 있음
    TOTAL_LENGTH(141, 0, PacketType.String);

    회원처리호가(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

}
