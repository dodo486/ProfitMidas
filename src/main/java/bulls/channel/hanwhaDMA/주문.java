package bulls.channel.hanwhaDMA;

import bulls.packet.OffsetLength;
import bulls.packet.PacketType;
public enum 주문 {
    HeaderBodyLength(0, 4, PacketType.Integer),
    HeaderMsgType(4, 1, PacketType.String),
    HeaderResponseCode(5, 4, PacketType.String),
    HeaderMsgSeqNum(9, 10, PacketType.Integer),
    HeaderMsgCount(19, 1, PacketType.Integer),
    TRCode(20, 1, PacketType.String),
    장구분(21, 2, PacketType.String),
    주문번호(23, 10, PacketType.String),
    원주문번호(33, 10, PacketType.String),
    종목코드(43, 12, PacketType.String),
    매도매수구분코드(55, 1, PacketType.String),
    정정취소구분코드(56, 1, PacketType.String),
    계좌번호(57, 12, PacketType.String),
    호가수량(69, 10, PacketType.Integer),
    호가가격(79, 11, PacketType.Float),
    시장가지정가(90, 1, PacketType.String),
    일반IOCFOK(91, 1, PacketType.String),
    IP주소(92, 12, PacketType.String),
    매도유형코드(104, 2, PacketType.String), //01:일반 02:공매도
    PT구분코드(106, 2, PacketType.String),
    사용자영역(108, 20, PacketType.String),
    북코드(108, 14, PacketType.String),
    FILLER1(122, 3, PacketType.String),
    Purpose(125, 1, PacketType.String),
    매도잔고유형(126, 1, PacketType.String), // 0:일반잔고 1:대차잔고 2:권리잔고 2021.07.08 미니원장 소스 추가
    FepOutputFileId(127, 1, PacketType.Integer), //0:해당 상품 거래소 프로세스 라운드 로빈, 1-n:미니원장 설정 파일에 따른 출력 파일 번호
//    주문유발자(124,4, PacketType.Binary),
    //1바이트추가전
    //TOTAL_LENGTH(128,0,PacketType.String);

    //1바이트추가후
    사전신고(128, 1, PacketType.String),
    TOTAL_LENGTH(129, 0, PacketType.String);

    주문(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }


    public static void main(String[] args) {
        String packet = "0109D000000000002451SG10170000246          KR701576000221099136100014000000002800000036100200100100031250022M:KR49999999KP      0";

        for (주문 data : 주문.values()) {
            String sb = data.toString() +
                    ":" +
                    data.parser().parseStr(packet);
            System.out.println(sb);
        }
    }

}
