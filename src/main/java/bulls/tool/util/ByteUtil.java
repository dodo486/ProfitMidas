package bulls.tool.util;


import java.nio.ByteBuffer;

public class ByteUtil {

    public static boolean isSame(byte[] src1, int src1Offset, byte[] src2, int src2Offset, int compareLength) {
        while (compareLength > 0) {
            if (src1[src1Offset] != src2[src2Offset])
                return false;
            src1Offset++;
            src2Offset++;
            compareLength--;
        }

        return true;
    }


    public static boolean isSame(byte[] src1, byte[] src2) {

        int i = 0;

        try {
            while (true) {
                if (src1[i] != src2[i])
                    return false;
                i++;

            }
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
    }


    public static byte[] flipAndReadBuffer(ByteBuffer buf) {

        buf.flip();
        int limit = buf.limit();
        byte[] bytes = new byte[limit];
        for (int i = 0; i < limit; i++) {
            bytes[i] = buf.get();
        }
        return bytes;
    }

    public static byte[] readBuffer(ByteBuffer buf, int start, int end) {
        int oldPosition = buf.position();
        int oldLimit = buf.limit();
        buf.position(start).limit(end);
        byte[] bytes = new byte[end - start];
        for (int i = 0; i < end - start; i++) {
            bytes[i] = buf.get();
        }
        buf.position(oldPosition).limit(oldLimit);
        return bytes;
    }

    public static void printByteBuffer(ByteBuffer buf) {
        byte[] readBytes = flipAndReadBuffer(buf);
        System.out.println("ByteBuffer Contents :" + new String(readBytes));
    }


    public static void putIntToByteBuffer(ByteBuffer buf, int value, int length) {

        byte[] temp = new byte[length];
        int counter = 1;
        while (true) {

            int num = value % 10;
            temp[length - counter] = (byte) (num + 48);

            if (value < 10)
                break;
            value = value / 10;
            counter++;
        }

        buf.put(temp);
    }

    public static byte[] IntToBytes(int value, int length) {

        byte[] temp = new byte[length];
        int counter = 1;
        while (true) {

            int num = value % 10;
            temp[length - counter] = (byte) (num + 48);

            if (value < 10)
                break;
            value = value / 10;
            counter++;
        }

        return temp;
    }


    public static void main(String[] args) {
        System.out.println(new String(ByteUtil.IntToBytes(-1, 1)));
    }

}
