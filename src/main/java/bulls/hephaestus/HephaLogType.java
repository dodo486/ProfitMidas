package bulls.hephaestus;

public enum HephaLogType {
    운영장애("운영장애"),
    주의("주의"),
    매매장애("매매장애"),
    시스템오류("시스템오류"),
    알림("알림");


    final String type;

    HephaLogType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
