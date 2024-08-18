package bulls.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import bulls.log.DefaultLogger;
import bulls.thread.GeneralCoreThread;
import bulls.tool.util.GeneralCoreTimer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SisePollingDataChecker {
    public String feedNICIP;
    public String fileName;
    public String trCode;
    public String market;
    public String highSpeedMulticastGroupIP;
    public int realPort;
    public String sendSystem;
    MulticastSocket soc;
    private Executor feedExecutor;
    private final boolean terminated = false;
    public AtomicInteger counter = new AtomicInteger(0);

    public SisePollingDataChecker(String feedNICIP, String fileName, String trCode, String market, String highSpeedMulticastGroupIP, int realPort, String sendSystem) {
        this.feedNICIP = feedNICIP;
        this.fileName = fileName;
        this.trCode = trCode;
        this.market = market;
        this.highSpeedMulticastGroupIP = highSpeedMulticastGroupIP;
        this.realPort = realPort;
        this.sendSystem = sendSystem;
    }

    public boolean init() {
        int port = realPort;
        try {
            String ip = highSpeedMulticastGroupIP.trim();
            int tmp = ip.indexOf('(');
            if (tmp > 0) {
                ip = ip.substring(0, tmp);
            }
            NetworkInterface eth0 = NetworkInterface.getByInetAddress(InetAddress.getByName(feedNICIP));

            InetAddress group = InetAddress.getByName(ip);
            soc = new MulticastSocket(port);
            soc.joinGroup(new InetSocketAddress(group, port), eth0);
            Thread t1 = new GeneralCoreThread(this::listen);
            t1.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void listen() {
        while (!terminated) {
            try {
                byte[] byteReceived = new byte[1024];
                DatagramPacket packet = new DatagramPacket(byteReceived, 1024);
                soc.receive(packet);

                byte[] trByte = new byte[5];
                if (packet.getLength() == 0)
                    return;
                System.arraycopy(packet.getData(), 0, trByte, 0, 5);
                String trCode = new String(trByte);
                if (trCode.equals(this.trCode)) {
                    if (counter.get() == 0) {
                        DefaultLogger.logger.info("[Sise Rcv] {} {} {} {} {} {}", fileName, market, sendSystem, highSpeedMulticastGroupIP, realPort, trCode);
                    }
                    counter.getAndIncrement();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ipPortData {
        public final String trCode;
        public final String ip;
        public final int port;
        public final String market;
        public final String sendSystem;
        public final String fileName;

        private ipPortData(String trCode, String ip, int port, String market, String sendSystem, String fileName) {
            this.trCode = trCode;
            this.ip = ip;
            this.port = port;
            this.market = market;
            this.sendSystem = sendSystem;
            this.fileName = fileName;
        }

        public static List<ipPortData> fromTrInfoList(String filename) {
            List<ipPortData> ipPortDataList = new ArrayList<>();
            InputStream is;

            is = SisePollingDataChecker.class.getResourceAsStream("/feedIpPortInfo/" + filename);
            if (is == null) {
                System.out.println("Failed to read sise polling data from " + filename + ". check resource");
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            try {
                while (true) {
                    String line = br.readLine();

                    if (line == null)
                        break;

                    String[] split = line.split(" ");
                    if (split.length < 5) {
                        System.out.println("Failed to parse TrInfoList data : " + line);
                        continue;
                    }

                    String trCode = split[0];
                    String ip = split[1];
                    int port;
                    try {
                        port = Integer.parseInt(split[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("Failed to parse TrInfoList data : " + line);
                        continue;
                    }
                    // length = split[3];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 4; i < split.length; i++) {
                        sb.append(split[i]).append(" ");
                    }
                    String fileName = sb.toString().trim();
                    ipPortData data = new ipPortData(trCode, ip, port, "unknown", "unknown", fileName);
                    ipPortDataList.add(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return ipPortDataList;
        }

        public static List<ipPortData> fromJson(String seoulPusan) {
            List<ipPortData> ipPortDataList = new ArrayList<>();
            InputStream is;

            if (seoulPusan.equalsIgnoreCase("seoul"))
                is = SisePollingDataChecker.class.getResourceAsStream("/sisePollingDataSeoul.json");
            else if (seoulPusan.equalsIgnoreCase("pusan"))
                is = SisePollingDataChecker.class.getResourceAsStream("/sisePollingDataPusan.json");
            else {
                System.out.println("Failed to read sise polling data from json. check resource");
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String json = br.lines().collect(Collectors.joining(System.lineSeparator()));
            try {
                HashMap<String, Object> result = new ObjectMapper().readValue(json, HashMap.class);
                if (result.containsKey("dataArray")) {
                    ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) result.get("dataArray");
                    for (HashMap<String, Object> map : list) {
                        ipPortData data = new ipPortData(
                                (String) map.get("trCode"),
                                (String) map.get("highSpeedMulticastGroupIP"),
                                (int) map.get("realPort"),
                                (String) map.get("market"),
                                (String) map.get("sendSystem"),
                                (String) map.get("fileName")
                        );
                        ipPortDataList.add(data);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return ipPortDataList;
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage : java -cp ./SisePollingDataChecker.jar common.net.SisePollingDataChecker [seoul|pusan] [ip] [json|TrInfoList_FileName]");
            return;
        }
        String seoulPusan = args[0];
        String feedNICIP = args[1];
        DefaultLogger.logger.info("Starting sise polling data checker for NIC IP {}", feedNICIP);

        String filename = args[2];
        List<ipPortData> ipPortDataList;
        if (filename.equalsIgnoreCase("json"))
            ipPortDataList = ipPortData.fromJson(seoulPusan);
        else
            ipPortDataList = ipPortData.fromTrInfoList(filename);

        if (ipPortDataList == null)
            return;

        ArrayList<SisePollingDataChecker> checkList = new ArrayList<>();
        for (ipPortData data : ipPortDataList) {
            SisePollingDataChecker checker = new SisePollingDataChecker(feedNICIP,
                    data.fileName,
                    data.trCode,
                    data.market,
                    data.ip,
                    data.port,
                    data.sendSystem);
            checker.init();
            checkList.add(checker);
        }

        Runnable r = () -> {
            int total = checkList.size();
            int rcv = 0, norcv = 0;
            ConcurrentSkipListMap<String, AtomicInteger> trCodeCountMap = new ConcurrentSkipListMap<>();
            ConcurrentSkipListMap<Integer, AtomicInteger> portCountMap = new ConcurrentSkipListMap<>();
            for (SisePollingDataChecker checker : checkList) {
                if (checker.counter.get() == 0) {
                    DefaultLogger.logger.error("[No Sise] {} {} {} {} {} {}", checker.fileName, checker.market, checker.sendSystem, checker.highSpeedMulticastGroupIP, checker.realPort, checker.trCode);
                    norcv++;
                } else {
                    rcv++;
                    trCodeCountMap.computeIfAbsent(checker.trCode, k -> new AtomicInteger(0)).addAndGet(checker.counter.get());
                    portCountMap.computeIfAbsent(checker.realPort, k -> new AtomicInteger(0)).addAndGet(checker.counter.get());
                }
            }
            DefaultLogger.logger.info("Received Sise {}/{}", rcv, total);

            System.out.println("===================== trCode =====================");
            for (var entry : trCodeCountMap.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue().get());
            }
            System.out.println("====================== Port  =====================");
            for (var entry : portCountMap.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue().get());
            }
        };
        GeneralCoreTimer t = new GeneralCoreTimer("Sise polling checker");
        t.scheduleAtFixedRate(r, 1000, 1000 * 10);
    }
}
