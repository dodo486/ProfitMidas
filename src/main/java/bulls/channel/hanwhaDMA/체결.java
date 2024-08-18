package bulls.channel.hanwhaDMA;

import bulls.packet.OffsetLength;
import bulls.packet.PacketType;
public enum 체결 {

    HeaderBodyLength(0, 4, PacketType.Integer),
    HeaderMsgType(4, 1, PacketType.String),
    HeaderResponseCode(5, 4, PacketType.String),
    HeaderMsgSeqNum(9, 10, PacketType.Integer),
    HeaderMsgCount(19, 1, PacketType.Integer),
    TR구분(20, 1, PacketType.String),
    주문ID(21, 10, PacketType.String),
    종목코드(31, 12, PacketType.String),
    _9자리코드(33, 9, PacketType.String),
    계좌번호(43, 12, PacketType.String),
    체결번호(55, 11, PacketType.Integer),
    체결가격(66, 11, PacketType.Float),
    체결수량(77, 10, PacketType.Integer),
    장구분(87, 2, PacketType.Float),
    체결시각(89, 9, PacketType.String),
    근월물체결가격(98, 11, PacketType.Float),
    원월물체결가격(109, 11, PacketType.Float),
    매도매수구분코드(120, 1, PacketType.String),
    사용자영역(121, 20, PacketType.String),
    북코드(121, 14, PacketType.String),//사용자영역
    FILLER1(135, 3, PacketType.String),//사용자영역
    Purpose(138,1,PacketType.String),
    매도잔고유형(139,1,PacketType.String),
    FepOutputFileId(140,1,PacketType.String),
    TOTAL_LENGTH(141, 0, PacketType.String);

    체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

}
