package bulls.feed.current.data.cmtyFuture;

import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.실시간상하한가Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.cmtyFuture.CmtyFuture_실시간상하한가;

public class CmtyFuture_실시간상하한가Feed extends Feed implements 실시간상하한가Feed {
    public CmtyFuture_실시간상하한가Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    public byte[] getCodeByte() {
        return CmtyFuture_실시간상하한가.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    @Override
    public String getSettingCode() {
        return CmtyFuture_실시간상하한가.실시간가격제한설정코드.parser().parseStr(rawPacket, "");
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public Integer get상한가() {
        return CmtyFuture_실시간상하한가.상한가.parser().parseIntWithLeadingSign(rawPacket);
    }

    @Override
    public Integer get하한가() {
        return CmtyFuture_실시간상하한가.하한가.parser().parseIntWithLeadingSign(rawPacket);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        if (isValidSignal())
            updateLimitPriceAndQuoter();
    }
}
