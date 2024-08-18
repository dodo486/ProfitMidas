package bulls.feed.current.data.etc;

import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;

/**
 * 오픈된 포트를 통해 UDP 수신은 되지만 듣기를 희망하지 않는 Tr
 */
public class FeedNotAllowed extends Feed {

    public FeedNotAllowed() {
        super(FeedTRCode.NotMath, new byte[0]);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        //do nothing
    }
}
