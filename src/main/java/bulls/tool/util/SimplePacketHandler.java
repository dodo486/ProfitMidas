package bulls.tool.util;

import bulls.datastructure.Pair;
import bulls.exception.MakePacketException;
import bulls.log.DefaultLogger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;


public class SimplePacketHandler {


//	// 클라이언트에만 쓰자.. 현재 속도 개무시중.
//	public static void sendPacketWithAsciiLengthHeader(DataOutputStream out, String output) throws IOException{
//
//		byte[] body = output.getBytes("UTF-8");
//		int len = body.length;
//		if( len > 9999) {
//			throw new IllegalArgumentException("packet length is longer than 10000, this try will be neglected");
//		}
//
//		String lenStr = String.format("%04d%s",len, output);
//		byte[] obuf = lenStr.getBytes();
//
//		synchronized (out) {
//			out.write(obuf);
//			out.flush();
//		}
//	}
//
//
//	public static byte[] receivePacketWithAsciiLenHeader(DataInputStream in){
//		try{
//
//			byte[] cbuf = new byte[4];
//
////			if( in.available() < 4){
////				System.err.println ( "Too short Packet !! Can't read 4 length byte from packet");
////				return null;
////			}
//			for( int i = 0 ; i < 4 ; i ++){
//
//				cbuf[i] = in.readByte();
//			}
//
//			String lenString = new String(cbuf);
//			int length = Integer.parseInt(lenString);
//			byte[] data = new byte[length];
//
//			for ( int i = 0 ; i < length ; i ++){
//				data[i] = in.readByte();
//			}
//
//			return data ;
//		} catch (NumberFormatException e ){
//			DefaultLogger.logger.error("error found", e);
//			return null;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			DefaultLogger.logger.error("error found", e);
//			return null;
//		}
//	}

    public static void sendPacketWithAsciiLengthHeader(DataOutputStream out, String output) throws IOException {

        byte[] body = output.getBytes(StandardCharsets.UTF_8);

        int len = body.length;

        byte[] lenBytes = intToBytesBigEndian(len);
        byte[] buf = new byte[4 + len];
        System.arraycopy(lenBytes, 0, buf, 0, 4);
        System.arraycopy(body, 0, buf, 4, len);

        synchronized (out) {
            out.write(buf);
            out.flush();
        }
    }


    public static void sendPacketWithAsciiLengthHeader(SocketChannel channel, String output) throws IOException {

        byte[] body = output.getBytes(StandardCharsets.UTF_8);

        int len = body.length;

        byte[] lenBytes = intToBytesBigEndian(len);
        byte[] buf = new byte[4 + len];
        System.arraycopy(lenBytes, 0, buf, 0, 4);
        System.arraycopy(body, 0, buf, 4, len);

        int parsedLen = bytesToIntBigEndian(lenBytes);
        if (parsedLen != len) {
            String msg = new String(buf);
            System.out.println("msg sent: " + len + "[" + msg + "]");
        }
        ByteBuffer buffer = ByteBuffer.wrap(buf);
//		synchronized (channel) {
//			channel.write(buffer);
//		}
//		buffer.clear();

        synchronized (channel) {
            channel.write(buffer);
            for (int i = 0; i < 10 && buffer.remaining() > 0; ++i) {
                try {
                    DefaultLogger.logger.info("Send buffer is full. Size : {} Retry count : {}, Channel Info: {}", buffer.remaining(), i + 1, channel);
                    Thread.sleep(200);
                    channel.write(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (buffer.remaining() > 0)
            throw new IOException("Socket send buffer full");
        buffer.clear();
    }


    public static byte[] receivePacketWithAsciiLenHeader(DataInputStream in) {
        try {

            byte[] cbuf = new byte[4];

//			if( in.available() < 4){
//				System.err.println ( "Too short Packet !! Can't read 4 length byte from packet");
//				return null;
//			}
            for (int i = 0; i < 4; i++) {

                cbuf[i] = in.readByte();
            }

            int length = bytesToIntBigEndian(cbuf);

//			if (length > 100)
//				DefaultLogger.logger.debug("long packet {}", length);

            byte[] data = new byte[length];

            for (int i = 0; i < length; i++) {
                data[i] = in.readByte();
            }

            return data;
        } catch (NumberFormatException e) {
            DefaultLogger.logger.error("error found", e);
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
            return null;
        }
    }


    public static String attachLengthHeader(String output) {
        int len = output.length();

        len = len + 10000;
        String lenStr = len + "";

        lenStr = lenStr.substring(1);
        String outPacket = lenStr + output;
        return outPacket;
    }


//	public static void sendPacketWithAsciiLengthHeader(OutputStream out, String output) throws IOException{
//		int len = output.length();
//
//		len = len + 10000;
//		String lenStr = new String(len +"");
//
//		lenStr = lenStr.substring(1);
//
//		String outPacket = lenStr + output;
//		byte[] obuf = new byte[len + 4];
//
//		obuf = outPacket.getBytes();
//		out.write(obuf);
//		out.flush();
//	}
//


    public static String receiveKrxRespond(DataInputStream in) {
        try {

//			System.out.println("IN : CurrentThread Receiving ontly Respond :" + Thread.currentThread().getName());
            byte[] cbuf = new byte[82];
            in.read(cbuf, 0, 82);
            if (cbuf == null)
                return null;

            byte[] lenBuf = new byte[6];
            System.arraycopy(cbuf, 8, lenBuf, 0, 6);
            int length = Integer.parseInt(new String(lenBuf));
            byte[] data = new byte[length];

            in.read(data, 0, length);
            String header = new String(cbuf);
            String body = new String(data);

            return header + body;
        } catch (NumberFormatException e) {
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    public static byte[] receiveKrxRespondByte(DataInputStream in) {
        try {

//			System.out.println("IN : CurrentThread using Data :" + Thread.currentThread().getName() );
            byte[] cbuf = new byte[82];
            in.read(cbuf, 0, 82);
            if (cbuf == null)
                return null;

            //System.out.println("RECEIVE HEADER [" + new String(cbuf) + "]");
            byte[] lenBuf = new byte[6];
            System.arraycopy(cbuf, 8, lenBuf, 0, 6);
            int length = Integer.parseInt(new String(lenBuf));
            byte[] data = new byte[length];
            in.read(data, 0, length);


            //System.out.println("RECEIVE BODY [" + new String(data) + "]");

            byte[] ret = new byte[cbuf.length + data.length];
            System.arraycopy(cbuf, 0, ret, 0, cbuf.length);
            System.arraycopy(data, 0, ret, cbuf.length, data.length);

            return ret;
        } catch (NumberFormatException e) {
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    public static Pair<byte[], byte[]> receiveKrxResponds(DataInputStream in) {
        try {

//			System.out.println("IN : CurrentThread using Data :" + Thread.currentThread().getName() );
            byte[] cbuf = new byte[82];
            in.read(cbuf, 0, 82);
            if (cbuf == null)
                return null;

            //System.out.println("RECEIVE HEADER [" + new String(cbuf) + "]");

            int length = 0;
            length += (cbuf[8] - 48) * 100000;
            length += (cbuf[9] - 48) * 10000;
            length += (cbuf[10] - 48) * 1000;
            length += (cbuf[11] - 48) * 100;
            length += (cbuf[12] - 48) * 10;
            length += (cbuf[13] - 48);

            byte[] data = new byte[length];
            in.read(data, 0, length);

            return new Pair<byte[], byte[]>(cbuf, data);
        } catch (NumberFormatException e) {
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    public static byte[] receiveKrxRespondBodyByte(DataInputStream in) {
        try {

            byte[] cbuf = new byte[82];
            in.read(cbuf, 0, 82);
            if (cbuf == null)
                return null;

            //System.out.println("RECEIVE HEADER [" + new String(cbuf) + "]");
            byte[] lenBuf = new byte[6];
            System.arraycopy(cbuf, 8, lenBuf, 0, 6);
            int length = Integer.parseInt(new String(lenBuf));
            byte[] data = new byte[length];
            in.read(data, 0, length);


            return data;

        } catch (NumberFormatException e) {
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    public static String receiveKoscomRespond(DataInputStream in) {

        try {

            byte[] cbuf = new byte[4];
            in.read(cbuf, 0, 4);
            if (cbuf == null)
                return null;

            int length = Integer.parseInt(new String(cbuf));
            byte[] data = new byte[length];

            in.read(data, 0, length);

            return new String(data);
        } catch (NumberFormatException e) {
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    public static String receiveBridgeDataFromBox(DataInputStream in) {

        try {

            byte[] cbuf = new byte[6];
            int count = in.read(cbuf, 0, 6);

            if (count < 6)
                return null;

            String lenString = new String(cbuf);
            int length = Integer.parseInt(lenString);
            byte[] data = new byte[length];

            in.read(data, 0, length);

            String dataPacket = new String(data);


            StringBuffer fullPacket = new StringBuffer();
            fullPacket.append(lenString);
            fullPacket.append(dataPacket);

            if (dataPacket.length() != length) {
                System.err.println("Unexpected Length Exception : " + fullPacket);
                return null;
            }

            String ret = fullPacket.toString();
            System.out.println("==========================================");
            System.out.println(" len:" + length);
            System.out.println("Box PACKET!!!:" + ret);
            System.out.println("==========================================");
            return ret;
        } catch (NumberFormatException e) {
            DefaultLogger.logger.error("error found", e);
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
            return null;
        }

    }

    public static byte[] intToBytesBigEndian(int i) {
        byte[] result = new byte[4];

        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i);

        return result;
    }

    public static int bytesToIntBigEndian(byte[] arr) {
        return (arr[0] & 0xff) << 24 | (arr[1] & 0xff) << 16 |
                (arr[2] & 0xff) << 8 | (arr[3] & 0xff);
    }

    public static int byteToShort(byte b1, byte b2) {

        int newValue = 0;
        newValue |= (((int) b1) << 8) & 0xFF00;
        newValue |= (((int) b2)) & 0xFF;

        return newValue;
    }


    public static byte[] charToByteArray(char c) {
        byte[] twoBytes = {(byte) (c & 0xff), (byte) (c >> 8 & 0xff)};
        return twoBytes;
    }


    // Pair<trCode,BodyList>
    public static Pair<Integer, ArrayList<byte[]>> receiveLeadingPacket(DataInputStream in) {
        try {
            byte[] cbuf = new byte[25];


            for (int i = 0; i < 25; i++) {

                cbuf[i] = in.readByte();
            }


            Integer commandId = byteToShort(cbuf[3], cbuf[4]);
//			System.out.println("receive leading :"+cbuf[3]);
//			System.out.println("receive leading :"+cbuf[4]);
//			System.out.println("CommandId " +commandId);

            byte[] lenByte = new byte[6];
            System.arraycopy(cbuf, 16, lenByte, 0, 6);
            String lenStr = new String(lenByte); //PacketSkeleton.getInstance("leadingPacketHeader.txt").createValue(cbuf, PacketSkeleton.HEADER_BODY_LENGTH);
            int length = Integer.parseInt(lenStr);

            byte[] dataCountByte = new byte[3];
            System.arraycopy(cbuf, 22, dataCountByte, 0, 3);
            String dataCountStr = new String(dataCountByte); // PacketSkeleton.getInstance("leadingPacketHeader.txt").createValue(cbuf, PacketSkeleton.HEADER_DATA_COUNT);
            int dataCount = Integer.parseInt(dataCountStr);


            ArrayList<byte[]> dataList = new ArrayList<byte[]>();

            for (int j = 0; j < dataCount; j++) {
                byte[] data = new byte[length];
                for (int i = 0; i < length; i++) {
                    data[i] = in.readByte();
                }
                dataList.add(data);
            }
            Pair<Integer, ArrayList<byte[]>> ret = new Pair<Integer, ArrayList<byte[]>>(commandId, dataList);
            return ret;
        } catch (NumberFormatException e) {
            DefaultLogger.logger.error("error found", e);
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
            return null;
        }
    }


    // Pair<trCode,BodyList>
    public static void sendLeadingPacket(DataOutputStream out, byte[] header, byte[] data) {
        try {

            int length = 0;
            length += header.length;
            length += data.length;

            byte[] wholePacket = new byte[length];

            System.arraycopy(header, 0, wholePacket, 0, header.length);
            System.arraycopy(data, 0, wholePacket, header.length, data.length);
            out.write(wholePacket);
            out.flush();

        } catch (NumberFormatException e) {
            DefaultLogger.logger.error("error found", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }
    }


    public static String receiveBridgeDataFromKoscom(DataInputStream in) {

        try {

            byte[] cbuf = new byte[6];

            for (int i = 0; i < 6; i++) {
                cbuf[i] = in.readByte();
            }

            String lenString = new String(cbuf);
            int length = Integer.parseInt(lenString);
            byte[] data = new byte[length];

            for (int i = 0; i < length; i++) {
                data[i] = in.readByte();
            }

            String dataPacket = new String(data);


            StringBuffer fullPacket = new StringBuffer();
            fullPacket.append(lenString);
            fullPacket.append(dataPacket);

            if (dataPacket.length() != length) {
                System.err.println("Unexpected Length Exception : " + fullPacket);
                return null;
            }

            String ret = fullPacket.toString();
            System.out.println("Koscom PACKET:" + ret + " len:" + length);
            return ret;
        } catch (NumberFormatException e) {
            DefaultLogger.logger.error("error found", e);
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
            return null;
        }

    }


    public static String receiveZenTecFepRespond(DataInputStream in) {

        byte[] fep = new byte[61];
        try {

            int readBytes = in.read(fep, 0, 61);

            if (readBytes <= 0)
                return null;

        } catch (IOException e) {
            DefaultLogger.logger.error("error found", e);
            return null;
        }

        return new String(fep);
    }


    public static String[] receiveZenTecDataAfterFep(DataInputStream in, int totalLength) {

        try {


            byte[] pb = new byte[162];
            in.read(pb, 0, 162);

            byte[] pbu = new byte[50];
            in.read(pbu, 0, 50);

            String[] result = new String[3];
            result[0] = new String(pb);
            result[1] = new String(pbu);

            int dataLength = totalLength - (61 + 162 + 50);

            if (dataLength > 0) {
                byte[] data = new byte[dataLength];
                in.read(data, 0, dataLength);
                result[2] = new String(data);
            }

            return result;


        } catch (NumberFormatException e) {
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }

    }


    public static String receiveZenTecPBHeader(DataInputStream in) {


        byte[] pb = new byte[162];
        try {
            in.read(pb, 0, 162);
            String s = new String(pb);
            return s;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }

        return null;

    }

    public static String receiveZenTecUserHeader(DataInputStream in) {

        byte[] pbu = new byte[50];

        try {
            in.read(pbu, 0, 50);
            String s = new String(pbu);
            return s;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }

        return null;
    }

    public static byte[] receiveZenTecData(DataInputStream in, int dataLength) {

        byte[] pbu = new byte[dataLength];

        try {
            in.read(pbu, 0, dataLength);
            return pbu;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }

        return null;
    }

    public static String receiveZenTecMessage(DataInputStream in, int dataLength) {

        byte[] pbu = new byte[dataLength];

        try {
            in.read(pbu, 0, dataLength);
            String s = new String(pbu);
            return s;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }

        return null;
    }

    public static String receiveZenTecMessage(DataInputStream in, int dataLength, String encoding) {

        byte[] pbu = new byte[dataLength];

        try {
            in.read(pbu, 0, dataLength);
            String s = new String(pbu, encoding);
            return s;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }

        return null;
    }

    public static void writeToFile(byte[] b) {
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream("c:\\java\\testt.txt", true), StandardCharsets.UTF_8));


            bw.write(new String(b));
            bw.newLine();
            bw.newLine();
            bw.close();

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }
    }

    public static void writeToFile(String s) {
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream("c:\\java\\testt.txt", true), StandardCharsets.UTF_8));


            bw.write(s);
            bw.newLine();
            bw.newLine();
            bw.close();

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }
    }


    public static String[] receiveZenTecDataTotalRespond(DataInputStream in) {

        try {

            byte[] fep = new byte[61];
            in.read(fep, 0, 61);

            byte[] pb = new byte[162];
            in.read(pb, 0, 162);

            byte[] pbu = new byte[50];
            in.read(pbu, 0, 50);

            byte[] len = new byte[5];

            System.arraycopy(fep, 1, len, 0, 5);
            int totalLength = Integer.parseInt(new String(len));
            int dataLength = totalLength - (61 + 162 + 50);

            String[] result = new String[4];
            result[0] = new String(fep);
            result[1] = new String(pb);
            result[2] = new String(pbu);

            if (dataLength > 0) {
                byte[] data = new byte[dataLength];
                in.read(data, 0, dataLength);
                result[3] = new String(data);
            }

            return result;


        } catch (NumberFormatException e) {
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    public static String receiveRespondAll(DataInputStream in) {

        try {

            String ret = "";
            byte[] cbuf = new byte[256];
            while (true) {
                int count = in.read(cbuf, 0, 256);
                ret += new String(cbuf);
                if (count <= 0)
                    break;
            }
            if (cbuf == null)
                return null;
            return ret;

        } catch (NumberFormatException e) {
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    public static String wrapPacketSizeInfo(String input) {
        if (input == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append("0000");
        sb.append(input);
        Integer lengthData = input.length();
        String lengthDataString = lengthData + "";

        try {
            smartInsert(sb, 0, 4, lengthDataString, true, '0');
        } catch (MakePacketException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        }

        return sb.toString();
    }

    public static void smartInsert(StringBuilder sb, int offset, int length, String data, boolean isTruncateRight, char blankCharacter) throws MakePacketException {

        int datalen = data.length();

        int offsetAdd = length - datalen;

        if (offsetAdd == 0) {
            sb.replace(offset, offset + length, data);
            return;
        }

        if (offsetAdd < 0) {
            throw new MakePacketException("Data is too long ");
        }


        char[] b = new char[offsetAdd];
        for (int i = 0; i < offsetAdd; i++)
            b[i] = blankCharacter;
        String blank = new String(b);

        if (isTruncateRight) {
            sb.replace(offset, offset + offsetAdd, blank);
            sb.replace(offset + offsetAdd, offset + length, data);
        } else {
            sb.replace(offset, offset + datalen, data);
            sb.replace(offset + datalen, offset + length, blank);
        }

    }


    public static Double longDouble2String(int size, double value) {

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(size);
        nf.setGroupingUsed(false);
        return Double.parseDouble(nf.format(value));
    }


    public static ArrayList<Pair<String, String>> tokenizeDATA(String input) {

        ArrayList<Pair<String, String>> ret = new ArrayList<Pair<String, String>>();

        int start = 0;
        int lengthOfExecBlock = 21;
        int totalLength = input.length();
        while (true) {
            String lengthString = input.substring(start, start + 3);
            int lengthOfBody = Integer.parseInt(lengthString);


            String execBlock = input.substring(start, start + lengthOfExecBlock);
            System.out.println(execBlock);
            String body = input.substring(start + lengthOfExecBlock, start + lengthOfExecBlock + lengthOfBody);
            System.out.println(body);
            ret.add(new Pair<String, String>(execBlock, body));

            start += (lengthOfExecBlock + lengthOfBody);

            if (start == totalLength)
                break;

        }

        return ret;
    }


    public static String zeroEliminatedIntegerString(String input) {

        try {

            Boolean isMinus = false;
            if (input.charAt(0) == '-')
                isMinus = true;

            String signAdjusted = null;
            if (input.charAt(0) == '+' || input.charAt(0) == '-')
                signAdjusted = input.substring(1);
            else
                signAdjusted = input;


            Integer temp = Integer.parseInt(signAdjusted);
            String zeroEliminated = temp + "";

            if (isMinus)
                zeroEliminated = "-" + zeroEliminated;

            return zeroEliminated;

        } catch (NumberFormatException e) {
            return input;
        }

    }
}
