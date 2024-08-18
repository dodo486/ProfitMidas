package bulls.order.enums;
public enum AccountType {
    일반("00"),
    시장조성자("10");


    private final String typeCode;

    AccountType(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getValue() {
        return typeCode;
    }
}
