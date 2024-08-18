package bulls.feed.file;

import bulls.designTemplate.GeneralFileReader;
import bulls.log.DefaultLogger;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class UniversalPcapFileReader implements GeneralFileReader {
    private final File feedFile;
    private final Protocol type;
    PcapHandle handle;
    HashMap<String, ByteArrayOutputStream> fragMap = new HashMap<>();

    public UniversalPcapFileReader(String feedFilePath, Protocol type) {
        this.feedFile = new File(feedFilePath);
        this.type = type;
    }

    private String generatePacketString(PcapHandle handle, byte[] b) throws UnsupportedEncodingException {
        if (b == null) {
            DefaultLogger.logger.info("packet is null");
            return null;
        } else if (b.length < 5) {
            DefaultLogger.logger.info("packet is shorter than 5 line={}", new String(b));
            return null;
        }

        StringBuilder sb = new StringBuilder();
        LocalDateTime t = handle.getTimestamp().toLocalDateTime();
        int currHour = t.getHour();
        int currMin = t.getMinute();
        int currSec = t.getSecond();
        int currMs = t.getNano();
        long fullTime = ((currHour * 100 + currMin) * 100 + currSec) * 1_000_000_000L + currMs;
        if (fullTime < 10_00_00_000_000_000L) { // 11_53_42_797_151_671
            sb.append('0');
        }
        sb.append(fullTime);
        sb.append(new String(b, "EUC-KR"));
        return sb.toString();
    }

    private String getTCP() {
        try {
            while (true) {
                byte[] b = null;
                Packet packet = handle.getNextPacketEx();
                if (packet == null) {
                    DefaultLogger.logger.info("packet is null. maybe finished.");
                    return null;
                }
                TcpPacket tp = packet.get(TcpPacket.class);

                if (tp == null) {
                    IpV4Packet ip = packet.get(IpV4Packet.class);
                    if (ip != null) {
                        String key = ip.getHeader().getSrcAddr().toString() + "_" + ip.getHeader().getDstAddr().toString() + "_" + ip.getHeader().getIdentificationAsInt();
                        ByteArrayOutputStream l;
                        if (fragMap.containsKey(key)) {
                            l = fragMap.get(key);
                        } else {
                            l = new ByteArrayOutputStream();
                            fragMap.put(key, l);
                        }
                        byte[] fragByteArr = ip.getPayload().getRawData();
                        try {
                            l.write(fragByteArr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (ip.getHeader().getMoreFragmentFlag()) {
                            continue;
                        } else {
                            try {
                                TcpPacket defragTcpPacket = TcpPacket.newPacket(l.toByteArray(), 0, l.size());
                                if (defragTcpPacket != null) {
                                    b = defragTcpPacket.getPayload().getRawData();
                                }
                            } catch (IllegalRawDataException e) {
                                e.printStackTrace();
                            }
                            fragMap.remove(key);
                        }
                    } else {
                        continue;
                    }
                } else {
                    try {
                        b = tp.getPayload().getRawData();
                    } catch (NullPointerException e) {
                        DefaultLogger.logger.info("packet has no data");
                        continue;
                    }
                }

                String packetString = generatePacketString(handle, b);
                if (packetString != null)
                    return packetString;
            }
        } catch (PcapNativeException | TimeoutException | NotOpenException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            DefaultLogger.logger.info("Pcap file reached end of file");
        }
        return null;
    }

    private String getUDP() {
        try {
            while (true) {
                byte[] b = null;
                Packet packet = handle.getNextPacketEx();
                if (packet == null) {
                    DefaultLogger.logger.info("packet is null. maybe finished.");
                    return null;
                }
                UdpPacket up = packet.get(UdpPacket.class);
                if (up == null) {
                    IpV4Packet ip = packet.get(IpV4Packet.class);
                    if (ip != null) {
                        String key = ip.getHeader().getSrcAddr().toString() + "_" + ip.getHeader().getDstAddr().toString() + "_" + ip.getHeader().getIdentificationAsInt();
                        ByteArrayOutputStream l;
                        if (fragMap.containsKey(key)) {
                            l = fragMap.get(key);
                        } else {
                            l = new ByteArrayOutputStream();
                            fragMap.put(key, l);
                        }
                        byte[] fragByteArr = ip.getPayload().getRawData();
                        try {
                            l.write(fragByteArr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (ip.getHeader().getMoreFragmentFlag()) {
                            continue;
                        } else {
                            try {
                                UdpPacket defragUdpPacket = UdpPacket.newPacket(l.toByteArray(), 0, l.size());
                                if (defragUdpPacket != null) {
                                    b = defragUdpPacket.getPayload().getRawData();
                                }
                            } catch (IllegalRawDataException e) {
                                e.printStackTrace();
                            }
                            fragMap.remove(key);
                        }
                    } else {
                        continue;
                    }
                } else {
                    b = up.getPayload().getRawData();
                }

                String packetString = generatePacketString(handle, b);
                if (packetString != null)
                    return packetString;
            }
        } catch (PcapNativeException | TimeoutException | NotOpenException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            DefaultLogger.logger.info("Pcap file reached end of file");
        }
        return null;
    }

    @Override
    public String next() {
        if (type == Protocol.UDP)
            return getUDP();
        else if (type == Protocol.TCP)
            return getTCP();

        return null;
    }

    @Override
    public boolean init() {
        try {
            System.out.println(feedFile.getPath());
            handle = Pcaps.openOffline(feedFile.getPath(), PcapHandle.TimestampPrecision.NANO);
            handle.setFilter(type.getTcpdumpParameter(), BpfProgram.BpfCompileMode.OPTIMIZE);
            return true;
        } catch (PcapNativeException e) {
            try {
                handle = Pcaps.openOffline(feedFile.getPath());
                handle.setFilter(type.getTcpdumpParameter(), BpfProgram.BpfCompileMode.OPTIMIZE);
                return true;
            } catch (PcapNativeException | NotOpenException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean close() {
        handle.close();
        handle = null;
        return true;
    }
}
