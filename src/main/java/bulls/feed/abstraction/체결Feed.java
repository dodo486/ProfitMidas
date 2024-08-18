package bulls.feed.abstraction;

import bulls.order.enums.LongShort;

public interface 체결Feed {

    String getRepresentingCode();

    String getBoardId();

    Integer getPrice();

    Integer getAmount();

    LongShort getLongShort();

    long getArrivalStamp();
}
