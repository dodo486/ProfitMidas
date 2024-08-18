package bulls.order.enums;

public enum 정정취소구분 {
    NEW(1),
    AMEND(2),
    CANCEL(3),
    UNDEFINED(4); // Contract 를 ChannelMsg 의 일부로 보기 위해 일단 선언.

    public int getValue() {
        return value;
    }

    public static 정정취소구분 valueOf(int value) {
        switch (value) {
            case '1':
                return 정정취소구분.NEW;
            case '2':
                return 정정취소구분.AMEND;
            case '3':
                return 정정취소구분.CANCEL;
            default:
                return 정정취소구분.UNDEFINED;
        }
    }

    int value;

    정정취소구분(int value) {
        this.value = value;
    }


}
