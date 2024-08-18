package bulls.feed.abstraction;

import bulls.staticData.LimitPriceInfoCenter;

public interface 가격제한폭확대Feed {

    String getRepresentingCode();

    Integer get상한가();

    Integer get하한가();

    default void updateLimitPriceAndQuoter() {
        String isinCode = getRepresentingCode();
        int 상한가 = get상한가();
        int 하한가 = get하한가();

        LimitPriceInfoCenter.Instance.updateLimitPriceInfo(isinCode, 상한가, 하한가);
    }
}