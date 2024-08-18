package bulls.feed.current.data.etc;

import bulls.exception.UnidentifiedTrCodeException;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;

public class UndefinedFeed extends Feed {

    public UndefinedFeed(FeedTRCode trCode, byte[] packet) throws UnidentifiedTrCodeException {
        super(trCode, packet);

//        throw new UnidentifiedTrCodeException("Illegal try to use Unidentified Feed Class, TrCode :" +strategyClass.getTrCodeStr() + " packet :" + new String(packet));
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        // do nothing
    }

}
