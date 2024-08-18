package bulls.channel.hanwhaDMA;

import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum 로그온 {

    HeaderBodyLength(0, 4, PacketType.Integer),
    HeaderMsgType(4, 1, PacketType.String),
    HeaderResponseCode(5, 4, PacketType.String),
    HeaderMsgSeqNum(9, 10, PacketType.Integer),
    HeaderMsgCount(19, 1, PacketType.Integer),
    아이디(20, 20, PacketType.String),
    패스워드(40, 20, PacketType.String),
    맥주소(60, 20, PacketType.String),
    TOTAL_LENGTH(80, 0, PacketType.String);

    로그온(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
