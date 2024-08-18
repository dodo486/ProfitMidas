package bulls.order.enums;

public enum IocFokType {
    일반((byte) '0'),
    IOC((byte) '3'),
    FOK((byte) '4'),
    CANCEL((byte) ' ');

    private final byte typeCode;

    IocFokType(byte typeCode) {
        this.typeCode = typeCode;
    }

    public byte getValue() {
        return typeCode;
    }

    public static IocFokType getType(byte typeCode) {
        for (IocFokType type : values()) {
            if (type.typeCode == typeCode)
                return type;
        }
        return 일반;
    }

}
