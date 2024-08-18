package bulls.feed.abstraction;

public interface 실시간상하한가Feed extends 가격제한폭확대Feed {

    String getSettingCode();

    default boolean isValidSignal() {
        return getSettingCode().equals("0");
    }
}
