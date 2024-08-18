package bulls.feed.abstraction;

import bulls.feed.current.enums.TRDescription;

import java.lang.reflect.Constructor;

public interface TRCodeInformation {

    byte[] getTrBytes();

    String getTrCodeStr();

    TRDescription getTrDescription();

    Class<? extends FeedParser> getParser();

    Constructor<? extends Feed> getConstructor();
}
