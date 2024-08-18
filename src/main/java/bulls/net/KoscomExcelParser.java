package bulls.net;

import bulls.feed.udpInfo.TrInfo;
import bulls.log.DefaultLogger;
import bulls.server.enums.ServerLocation;
import bulls.tool.util.RegExCenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class KoscomExcelParser {

    // 엑셀 ctrl+j 가 개행문자 .. 붙여넣기 전에 싹 다 치환 필요 일차적으로 엑셀에 싹 다 모으고 치환한 후에 resource file 로 업로드 하세...
    // 코스콤 시세 엑셀 파일로부터 "전송IP및포트" 탭의 정보를 ipPortInfo_speedup 에 죄다 붙여넣고
    // "TR목록" 탭의 내용을 lengthInfo에 붙여넣고( 설명 컬럼은 제외 ) 실행하여 나온 결과 stdout 을 trInfoList로 붙여넣음..
    // 실제 프로그램이 돌아갈때 시세 관련 접속 정보는 trInfoList에서 읽어옴
    // 서울/부산 시세 이분화에 따라 inputStream 은 로케이션에 따라서 주석처리하고 돌리자

    public static void main(String[] args) throws IOException {
        int trCodePos;
        int portPos;
        ServerLocation location = ServerLocation.SEOUL;
        InputStream ipPortIs;
        InputStream lengthIs;
        boolean isTestPort = false;

        if (location == ServerLocation.SEOUL) {
            ipPortIs = KoscomExcelParser.class.getResourceAsStream("/feedIpPortInfo/ipPortInfo_speedup");
            lengthIs = KoscomExcelParser.class.getResourceAsStream("/feedIpPortInfo/lengthInfo");
            trCodePos = 5;
            if (isTestPort)
                portPos = 3;
            else
                portPos = 2;
        } else {
            ipPortIs = KoscomExcelParser.class.getResourceAsStream("/feedIpPortInfo/PusanIpPortInfo");
            lengthIs = KoscomExcelParser.class.getResourceAsStream("/feedIpPortInfo/PusanLengthInfo");
            trCodePos = 4;
            portPos = 1;
        }

        HashMap<String, TrInfo> trMap = new HashMap<>();

        BufferedReader brLength = new BufferedReader(new InputStreamReader(lengthIs));

        String line;
        while ((line = brLength.readLine()) != null) {
//			line = line.replaceAll("\\t"," ").trim();
            String[] r = line.split("\\t");

            if (r.length < 3)
                continue;

            try {
                Integer length = Integer.parseInt(r[2]);
                TrInfo info = new TrInfo(r[1], length);
                info.setDescription(r[0]);
                trMap.put(r[1], info);
            } catch (NumberFormatException e) {
                System.err.println("unexpected line [" + line + "]");
            }
        }

        BufferedReader brIpPort = new BufferedReader(new InputStreamReader(ipPortIs));

        String ip = null;
        int port = 0;
        while ((line = brIpPort.readLine()) != null) {
//			line = line.replaceAll("\\t"," ").trim();

            String[] r = line.split("\\t");

            // ip, port 정보 포함 라인
            if (r.length > 4) {
                ip = r[0];
                ip = RegExCenter.Instance.extractFirstIp(ip);
                port = Integer.parseInt(r[portPos]);

                String trString = r[trCodePos];

                trString = trString.replaceFirst("\"", "");

                String[] tr = trString.split(" ");

                TrInfo info = trMap.get(tr[0]);

                if (info == null) {
                    System.err.println("Unidentified TrCode [" + line + "]");
                } else {
                    info.addExclusiveIpPort(ip, port);
                }
            } else { // TR 이름...
                String[] tr = r[0].split(" ");

                TrInfo info = trMap.get(tr[0]);
                if (ip == null || port == 0) {
                    System.err.println("Ip or port feedConnectionInfo missing for [" + line + "]");
                }

                if (info == null) {
                    System.err.println(" no LengthInfo for [" + line + "]");
                    continue;
                }
                info.addExclusiveIpPort(ip, port);
            }
        }

        for (Map.Entry<String, TrInfo> en : trMap.entrySet()) {
            if (en.getValue().ipPort.size() == 0) {
                System.err.println("Following TR exist in lengthInfo file while not in ipPortInfo_speedup :" + en.getKey());
            }
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            DefaultLogger.logger.error("error found", e);
        }
        for (Map.Entry<String, TrInfo> en : trMap.entrySet()) {
            en.getValue().print();
        }
    }
}
