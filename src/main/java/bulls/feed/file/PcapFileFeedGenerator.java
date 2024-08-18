package bulls.feed.file;

import bulls.designTemplate.GeneralFileReader;
import bulls.log.DefaultLogger;
import org.pcap4j.core.*;
import org.pcap4j.packet.IllegalRawDataException;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class PcapFileFeedGenerator implements GeneralFileReader {
    private final File feedFile;
    PcapHandle handle;
    HashMap<String, ByteArrayOutputStream> fragMap = new HashMap<>();

    public PcapFileFeedGenerator(String feedFilePath) {
        this.feedFile = new File(feedFilePath);
    }

    public byte[] nextByteArray() {
        try {
            while (true) {
                Packet p = null;
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
                if (b != null && b.length < 10) {
                    DefaultLogger.logger.info("packet is shorter than 10 line={}", new String(b));

                    continue;
                }
                StringBuilder sb = new StringBuilder();
                LocalDateTime t = handle.getTimestamp().toLocalDateTime();
                int currHour = t.getHour();
                int currMin = t.getMinute();
                int currSec = t.getSecond();
                int currMs = t.getNano() / 1000000;
                long fullTime = currHour * 10000000 + currMin * 100000 + currSec * 1000 + currMs;
                if (fullTime >= 100000000) {
                    sb.append(fullTime);
                } else {
                    sb.append('0');
                    sb.append(fullTime);
                }
                byte[] ret = new byte[9 + b.length];
                System.arraycopy(sb.toString().getBytes(StandardCharsets.US_ASCII), 0, ret, 0, 9);
                System.arraycopy(b, 0, ret, 9, b.length);
                return ret;
            }
        } catch (PcapNativeException | TimeoutException | NotOpenException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            DefaultLogger.logger.info("Pcap file reached end of file");
        } catch (Exception e) {
            DefaultLogger.logger.info("Pcap file unexpectedly finished...");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String next() {
        try {
            while (true) {
                Packet p = null;
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
                if (b != null && b.length < 5) {
                    DefaultLogger.logger.info("packet is shorter than 5 line={}", new String(b));

                    continue;
                }
                StringBuilder sb = new StringBuilder();
                LocalDateTime t = handle.getTimestamp().toLocalDateTime();
                int currHour = t.getHour();
                int currMin = t.getMinute();
                int currSec = t.getSecond();
                int currMs = t.getNano() / 1000000;
                long fullTime = currHour * 10000000 + currMin * 100000 + currSec * 1000 + currMs;
                if (fullTime >= 100000000) {
                    sb.append(fullTime);
                } else {
                    sb.append('0');
                    sb.append(fullTime);
                }
                sb.append(new String(b, "EUC-KR"));
                return sb.toString();
            }
        } catch (PcapNativeException | TimeoutException | NotOpenException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            DefaultLogger.logger.info("Pcap file reached end of file");
        }
        return null;
    }

    @Override
    public boolean init() {
        try {

            handle = Pcaps.openOffline(feedFile.getPath(), PcapHandle.TimestampPrecision.NANO);
            handle.setFilter("dst net 233.0.0.0/8 and udp", BpfProgram.BpfCompileMode.OPTIMIZE);
            return true;
        } catch (PcapNativeException e) {
            try {
                handle = Pcaps.openOffline(feedFile.getPath());
                handle.setFilter("dst net 233.0.0.0/8 and udp", BpfProgram.BpfCompileMode.OPTIMIZE);
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