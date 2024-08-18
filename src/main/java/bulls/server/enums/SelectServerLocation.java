package bulls.server.enums;

public enum SelectServerLocation {
    SEOUL,
    PUSAN,
    ALL,
    NOTHING;

    public boolean containsSeoul() {
        return (this == SEOUL) || (this == ALL);
    }

    public boolean containsPusan() {
        return (this == PUSAN) || (this == ALL);
    }
}
