package bulls.order.enums;

public enum LPorNot {
    LP(0),
    NotLP(1);

    LPorNot(int value) {
        this.value = value;
    }

    private final int value;


//
//    @JsonCreator
//    public static LPorNot forValue(int value) {
//        return  LPorNot.forValue(value);
//    }
//
//    @JsonValue
//    public int toValue() {
//        return value;
//    }
}
