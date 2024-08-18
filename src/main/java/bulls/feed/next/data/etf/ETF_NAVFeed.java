package bulls.feed.next.data.etf;

import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.etf.ETF_NAV;

public class ETF_NAVFeed extends Feed {

    public ETF_NAVFeed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        String code = getCode();
        Double nav = ETF_NAV.NAV.parser().parseDoubleInsertDot(rawPacket);
        dc.updateNav(code, nav);
    }

    public byte[] getCodeByte() {
        return ETF_NAV.isinCodeEtf.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }
}
