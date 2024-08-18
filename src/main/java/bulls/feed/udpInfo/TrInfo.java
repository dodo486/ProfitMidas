package bulls.feed.udpInfo;

import bulls.datastructure.Pair;

import java.util.ArrayList;

public class TrInfo {

    public final String trCode;
    public final int length;
    private String description;

    public ArrayList<Pair<String, Integer>> ipPort = new ArrayList<>();

    public TrInfo(String trCode, int length) {
        this.length = length;
        this.trCode = trCode;
    }

    public void addExclusiveIpPort(String ipAddr, int port) {
        Pair<String, Integer> p = new Pair<>(ipAddr, port);

        if (!ipPort.contains(p))
            ipPort.add(new Pair<>(ipAddr, port));
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void print() {
        for (Pair<String, Integer> p : ipPort) {
            String sb = trCode + " " + p.firstElem + " " + p.secondElem + " " + length + " " + description;
            System.out.println(sb);
        }
    }
}
