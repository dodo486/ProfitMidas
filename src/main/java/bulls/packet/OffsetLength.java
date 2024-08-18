package bulls.packet;

import bulls.channel.hanwhaDMA.주문;
import bulls.tool.util.ByteUtil;
import bulls.tool.util.SimpleMath;
import net.openhft.chronicle.bytes.Bytes;
import org.apache.commons.math3.util.FastMath;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public final class OffsetLength {

    public OffsetLength(int offSet, int length, PacketType type) {
        this.offSet = offSet;
        this.length = length;
        this.type = type;
    }

    private final int offSet;
    private final int length;
    private final PacketType type;

    public int getOffset() {
        return offSet;
    }

    public int getLength() {
        return length;
    }

    public PacketType getType() {
        return type;
    }

    public byte[] parseByte(byte[] packet) {
        byte[] frag = new byte[length];
        System.arraycopy(packet, offSet, frag, 0, length);
        return frag;
    }

    public byte parseSingleByte(byte[] packet) {
        return packet[offSet];
    }

    public String parseStr(String packetStr) {
        return packetStr.substring(offSet, offSet + length);
    }


    public String parseStr(byte[] packet) throws UnsupportedEncodingException {
        return new String(packet, offSet, length, "EUC-KR");
    }

    public String parseStr(byte[] packet, String altString) {
        try {
            return new String(packet, offSet, length, "EUC-KR");
        } catch (UnsupportedEncodingException e) {
            return altString;
        }
    }

    public String concatAndParse(byte[] packet, OffsetLength tail) {
        int totalLen = length + tail.length;
        byte[] bytes = new byte[totalLen];
        System.arraycopy(packet, offSet, bytes, 0, length);
        System.arraycopy(packet, tail.offSet, bytes, length, tail.length);

        return new String(bytes);
    }

    public void setStrPacket(byte[] packet, String value) {
        byte[] byteStr = value.getBytes();
        int len = byteStr.length;
        for (int i = 0; i < (length - len); i++) {
            packet[offSet + len + i] = ' ';
        }
        System.arraycopy(byteStr, 0, packet, offSet, len);
    }

    public void setByteStrPacket(byte[] packet, byte[] bytes) {
        int len = bytes.length;
        for (int i = 0; i < (length - len); i++) {
            packet[offSet + len + i] = ' ';
        }
        System.arraycopy(bytes, 0, packet, offSet, len);
    }

    public int parseIntFromPointedDouble(byte[] packet) {
        int value = 0;
        int figure = 1;
        for (int i = 0; i < length; i++) {

            // '/' ascii 값은 무시하자.
            if (packet[(offSet + length - 1) - i] < 46 || packet[(offSet + length - 1) - i] > 57) {
                byte[] error = new byte[length];
                System.arraycopy(packet, offSet, error, 0, length);
                String sb = "Offset:" +
                        offSet +
                        " Length:" +
                        length +
                        " parseInt from pointed Double fail on :" +
                        new String(error) +
                        " Pull Packet [" +
                        new String(packet) +
                        "]";
                throw new NumberFormatException(sb);
            }

            if (i < 2) {
                value += (packet[(offSet + length - 1) - i] - 48) * figure;
                figure *= 10;
            } else if (i == 2) {
                continue;   // 소숫점은 건너 뛴다.
            } else {
                value += (packet[(offSet + length - 1) - i] - 48) * figure;
                figure *= 10;
            }

        }
        return value;
    }


    public Double parseDoubleInsertDot(byte[] packet) {
        return parseDoubleInsertDot(packet, 2);
    }

    public Double parseDoubleInsertDot(byte[] packet, int decimalPoint) {
        byte[] frag = parseByte(packet);
        byte[] dotInserted = new byte[length + 1];
        System.arraycopy(frag, 0, dotInserted, 0, length);
        dotInserted[length - decimalPoint] = '.';
        System.arraycopy(frag, length - decimalPoint, dotInserted, length - decimalPoint + 1, decimalPoint);
        Double value = Double.parseDouble(new String(dotInserted));
        return value;
    }

    public Double parseDouble(byte[] packet) {
        byte[] frag = parseByte(packet);
        Double value = Double.parseDouble(new String(frag));
        return value;
    }

    public int parseIntWithLeadingSign(byte[] packet) {
        int ret = parseInt(packet);
        if (packet[offSet - 1] == '-')
            return -ret;
        else
            return ret;
    }

    public double parseFloatWithLeadingSign(byte[] packet, int decimalPoint) {
        double ret = parseDoubleInsertDot(packet, decimalPoint);
        if (packet[offSet - 1] == '-')
            return -ret;
        else
            return ret;
    }

    public int parseInt(byte[] packet) {
        int value = 0;
        int figure = 1;
        for (int i = 0; i < length - 1; i++) {
            if (packet[(offSet + length - 1) - i] < 48 || packet[(offSet + length - 1) - i] > 57) {
                byte[] error = new byte[length];
                System.arraycopy(packet, offSet, error, 0, length);
                String sb = "Offset:" +
                        offSet +
                        " Length:" +
                        length +
                        " parseInt fail on :" +
                        new String(error) +
                        " Full Packet [" +
                        new String(packet) +
                        "]";
                throw new NumberFormatException(sb);
            }
            value += (packet[(offSet + length - 1) - i] - 48) * figure;
            figure *= 10;
        }

        if (packet[offSet] == '-') {
            return -value;
        } else if (packet[offSet] < 48 || packet[offSet] > 57) {
            byte[] error = new byte[length];
            System.arraycopy(packet, offSet, error, 0, length);
            String sb = "Offset:" +
                    offSet +
                    " Length:" +
                    length +
                    " parseInt fail on :" +
                    new String(error) +
                    " Full Packet [" +
                    new String(packet) +
                    "]";
            throw new NumberFormatException(sb);
        }

        value += (packet[offSet] - 48) * figure;
        return value;
    }


    public Long parseLong(byte[] packet) {
        long value = 0;
        for (int i = 0; i < length; i++) {
            value += (packet[offSet + i] - 48) * SimpleMath.power(10, length - i - 1);
        }
        return value;
    }

    public Long parseLongWithLeadingSign(byte[] packet) {
        Long ret = parseLong(packet);
        if (packet[offSet - 1] == '-')
            return -ret;
        else
            return ret;
    }

    public Integer parseInt(String packetStr) {
        String intStr = parseStr(packetStr);
        return Integer.parseInt(intStr);
    }


    public void setByteFull(ByteBuffer buf, byte defaultByte) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = defaultByte;
        }

        setBytes(buf, bytes);
    }


    public void setString(ByteBuffer buf, String value) {

        byte[] valueBytes = value.getBytes();
        setBytes(buf, valueBytes);

        int surplus = length - valueBytes.length;
        while (surplus > 0) {
            buf.put((byte) ' ');
            surplus--;
        }
    }

    public void setBytes(ByteBuffer buf, byte[] bytes) {
        buf.put(bytes);
    }

    public void setBytesWithSize(ByteBuffer buf, byte[] bytes) {
        buf.put(bytes);
        int surplus = length - bytes.length;
        while (surplus > 0) {
            buf.put((byte) ' ');
            surplus--;
        }
    }

    public Integer setIntFast(byte[] packet, int value) {

        byte[] temp = new byte[length];

        for (int i = 0; i < length; i++) {
            int num = value % 10;
            if (value == 0) {
                temp[length - i - 1] = '0';
            } else if (value < 10) {
                temp[length - i - 1] = (byte) (num + 48);
                value = 0;
            } else {
                temp[length - i - 1] = (byte) (num + 48);
                value = value / 10;
            }
        }
        if (value < 0)
            temp[0] = '-';

        System.arraycopy(temp, 0, packet, offSet, length);

        return value;
    }

    public void setIntWithDot(byte[] packet, int value) {

        int totalLength = (int) FastMath.log10(value) + 2;
        int intValue = (int) (value / 100.0);
        int floatValue = value % 100;

        int intPartLength = totalLength - 3; // .xx 제외

        //set int part blank
        for (int i = 0; i < (length - totalLength); i++) {
            packet[offSet + i] = '0';
        }

        // 정수 부분이 있다면 ...
        if (intPartLength > 0) {

            byte[] intBytes = ByteUtil.IntToBytes(intValue, intPartLength);
            // copy int value
            System.arraycopy(intBytes, 0, packet, offSet + length - totalLength, intPartLength);
        }

        //소수 부분
        byte[] floatBytes = ByteUtil.IntToBytes(floatValue, 2);

        // set dot "."
        packet[offSet + length - 3] = '.';

        // switch blank to zero if float part is like  '.0x'
        if (floatBytes[0] == 0)
            floatBytes[0] = 48;

        packet[offSet + length - 2] = floatBytes[0];
        packet[offSet + length - 1] = floatBytes[1];

    }

    public int getFigure(int value) {
        if (value < 0) value = -value;
        if (value > 999999999) return 10;
        else if (value > 99999999) return 9;
        else if (value > 9999999) return 8;
        else if (value > 999999) return 7;
        else if (value > 99999) return 6;
        else if (value > 9999) return 5;
        else if (value > 999) return 4;
        else if (value > 99) return 3;
        else if (value > 9) return 2;
        else return 1;
    }


    public static void main(String[] args) {
        ByteBuffer b = ByteBuffer.allocateDirect(163);
        int value = 37243;
        b.position(주문.호가가격.parser().offSet);
        주문.호가가격.parser().setIntWithDot(b, value);

        String s = new String(ByteUtil.readBuffer(b, 0, 163));
        System.out.println(s);
    }

    public void setIntWithDot(ByteBuffer buf, int value) {
        if (length < 3)
            throw new IllegalArgumentException("Packet Length Error!!  Minimum length for float packet is 3 ");

        byte[] temp = new byte[length];
        //set int part blank
        for (int i = 0; i < length; i++) {
            temp[i] = '0';
        }

        if (value < 0) {
            value = -value;
            temp[0] = '-';
        }

        int totalLength = getFigure(value) + 1;
        int intValue = (int) (value / 100.0);
        int floatValue = value % 100;

        int intPartLength = totalLength - 3; // .xx 제외

        // 정수 부분이 있다면 ...
        if (intPartLength > 0) {

            byte[] intBytes = ByteUtil.IntToBytes(intValue, intPartLength);
            // copy int value
            System.arraycopy(intBytes, 0, temp, length - totalLength, intPartLength);
        }

        //소수 부분
        byte[] floatBytes = ByteUtil.IntToBytes(floatValue, 2);

        // set dot "."
        temp[length - 3] = '.';

        // switch blank to zero if float part is like  '.0x'
        if (floatBytes[0] == 0)
            floatBytes[0] = 48;

        temp[length - 2] = floatBytes[0];
        temp[length - 1] = floatBytes[1];

        buf.put(temp);
    }


    public void setByte(ByteBuffer buf, byte b) {
        buf.put(b);
    }

    public void setIntFast(ByteBuffer buf, int value) {

        byte[] temp = new byte[length];
        boolean minus = value < 0;
        if (minus)
            value = -1 * value;
        for (int i = 0; i < length; i++) {
            int num = value % 10;


            if (value == 0) {
                temp[length - i - 1] = '0';
            } else if (value < 10) {
                temp[length - i - 1] = (byte) (num + 48);
                value = 0;
            } else {
                temp[length - i - 1] = (byte) (num + 48);
                value = value / 10;
            }
        }
        if (minus)
            temp[0] = '-';
        buf.put(temp);
    }

    public void setLongFast(ByteBuffer buf, long value) {

        byte[] temp = new byte[length];

        for (int i = 0; i < length; i++) {
            long num = value % 10;

            if (value == 0) {
                temp[length - i - 1] = '0';
            } else if (value < 10) {
                temp[length - i - 1] = (byte) (num + 48);
                value = 0;
            } else {
                temp[length - i - 1] = (byte) (num + 48);
                value = value / 10;
            }
        }

        buf.put(temp);
    }

    public void setLongChronicle(ByteBuffer buf, long value) {

        Bytes bytes = Bytes.elasticByteBuffer();
        bytes.prepend(value);
    }
}
