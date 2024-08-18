package bulls.server.enums;

import bulls.exception.InvalidLocationException;
public enum ServerLocation {
    SEOUL("SEOUL", "서울"),
    PUSAN("PUSAN", "부산"),
    NA("NA", "미지정");

    private final String locationString;
    private final String koreanLocationString;

    ServerLocation(String locationString, String korean) {
        this.locationString = locationString;
        koreanLocationString = korean;
    }

    public String getLocationString() {
        return locationString;
    }

    public String getKoreanLocationString() {
        return koreanLocationString;
    }

    public static ServerLocation getValue(String name) throws InvalidLocationException {
        name = name.toUpperCase();
        for (ServerLocation value : values()) {
            if (value.locationString.equals(name))
                return value;
            if (value.koreanLocationString.equals(name))
                return value;
        }
        throw new InvalidLocationException(name);
    }
}
