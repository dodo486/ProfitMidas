package bulls.tool.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum RegExCenter {
    Instance;

    private final String rxIP = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    private final Pattern ipPattern = Pattern.compile(rxIP);
    private final String rxPacketFromFepLog = "0121D.{136}";
    private final Pattern packetFromFepLogPattern = Pattern.compile(rxPacketFromFepLog);
    private final String rxServerLogTime = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]\\.\\d{1,3}";
    private final String rxFepLogTime = "([01]?[0-9]|2[0-3])[0-5][0-9][0-5][0-9]\\.\\d{1,9}";
    private final Pattern serverTimePattern = Pattern.compile(rxServerLogTime);
    private final Pattern fepTimePattern = Pattern.compile(rxFepLogTime);


    public static void main(String[] args) {
        // TODO Auto-generated method stub


//        String data = "8318 [main]  08:00:13.448 [INFO] [main@c.e.PDFBuilder:73] -  PDF KODEX 200(KR7069500007) 의 Equity종목 롯데케미칼(KR7011170008) 의 기준가: 255000 , PDF 수량: 28.0\n";
        String data = "    54 [NML|lf_tr_fm001|073057.537666340|     75683|TcpFuncsCore.c| 592] SESSION REQUEST FROM CLIENT IP:10.10.1.123, PORT:54200\n";

        Matcher m = RegExCenter.Instance.fepTimePattern.matcher(data);

        while (m.find())
            System.out.println(m.group());

        System.out.println(data.matches(RegExCenter.Instance.rxFepLogTime));
    }

    public String extractFirstPacketFromFepLog(String source) {
        Matcher m = packetFromFepLogPattern.matcher(source);
        if (m.find())
            return m.group();

        else
            return null;
    }

    public String extractFirstIp(String source) {
        Matcher m = ipPattern.matcher(source);
        if (m.find())
            return m.group();

        else
            return null;
    }
}
