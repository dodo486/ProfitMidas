package bulls.feed.file;

public enum Protocol {
    TCP("tcp"),
    UDP("udp");

    private final String tcpdumpParameter;

    Protocol(String tcpdumpParameter) {
        this.tcpdumpParameter = tcpdumpParameter;
    }

    public String getTcpdumpParameter() {
        return tcpdumpParameter;
    }
}
