package bulls.channel.hanwhaDMA;

import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 증거금 {
    HeaderBodyLength(0, 4, PacketType.Integer),
    HeaderMsgType(4, 1, PacketType.String),
    HeaderResponseCode(5, 4, PacketType.String),
    HeaderMsgSeqNum(9, 10, PacketType.Integer),
    HeaderMsgCount(19, 1, PacketType.Integer),
    TR구분(20, 1, PacketType.String),
    계좌번호(21, 12, PacketType.String),
    사용한도(33, 18, PacketType.Float),
    총한도(51, 18, PacketType.Float),
    TOTAL_LENGTH(69, 0, PacketType.String);


    증거금(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
