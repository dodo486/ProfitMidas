package bulls.feed.test;

import bulls.feed.abstraction.FeedParser;
import bulls.feed.abstraction.TRCodeInformation;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FeedParserChecker {
    private final Map<String, Class<? extends FeedParser>> parserMap;
    private final boolean printUnknownPacket;

    public FeedParserChecker(boolean printUnknownPacket) {
        parserMap = new HashMap<>();
        this.printUnknownPacket = printUnknownPacket;
    }

    public void add(String trCode, Class<? extends FeedParser> parserClass) {
        if (!parserClass.isEnum()) {
            System.out.println("주어진 클래스는 enum이 아니므로 추가할 수 없습니다. trCode=" + trCode + ", class=" + parserClass.getName());
            return;
        }

        if (parserMap.containsKey(trCode))
            System.out.println("주의 : 해당 trCode에 대한 Parser 클래스가 이미 존재합니다. trCode=" + trCode + ", class=" + parserMap.get(trCode).getName());

        System.out.println("Parser 등록 : trCode=" + trCode + ", class=" + parserClass.getName());
        parserMap.put(trCode, parserClass);
    }

    public void add(Map<String, Class<? extends FeedParser>> parserMap) {
        for (var entry : parserMap.entrySet())
            add(entry.getKey(), entry.getValue());
    }

    public void add(TRCodeInformation information) {
        add(information.getTrCodeStr(), information.getParser());
    }

    public void check(String packetString) {
        String trCode = packetString.substring(0, 5);
        if (!parserMap.containsKey(trCode)) {
            if (printUnknownPacket)
                System.out.println("Unknown Packet : " + packetString);

            return;
        }

        System.out.println("============================================STRAT : " + trCode);
        System.out.println("Raw Packet : " + packetString);
        try {
            byte[] packetBytes = packetString.getBytes("EUC-KR");
            var clazz = parserMap.get(trCode);
            for (FeedParser p : clazz.getEnumConstants()) {
                String str;
                Object o;

                str = p.parser().parseStr(packetBytes);
                switch (p.parser().getType()) {
                    case Integer -> {
                        char maybeSign = str.charAt(0);
                        if (maybeSign == '-' || maybeSign == '+' || maybeSign == ' ')
                            o = p.parser().parseLongWithLeadingSign(packetBytes);
                        else
                            o = p.parser().parseLong(packetBytes);
                    }
                    case Float -> {
                        o = p.parser().parseDouble(packetBytes);
                    }
                    default -> {
                        o = str;
                    }
                }

                System.out.println(p + " : " + o + " (String : " + p.parser().parseStr(packetBytes) + ")");
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("파싱 실패 : 해당 패킷은 EUC-KR이 아닙니다.");
        }
        System.out.println("============================================END");
    }
}
