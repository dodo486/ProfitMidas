package bulls.packet;

public enum PacketType {
    String(0),
    Integer(1),
    Float(2),
    Binary(3),
    IntegerArray_5(4),
    IntegerArray_10(5);

    PacketType(int key) {

    }
}
