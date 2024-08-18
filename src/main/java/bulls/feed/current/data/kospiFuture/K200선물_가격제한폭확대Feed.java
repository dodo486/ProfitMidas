package bulls.feed.current.data.kospiFuture;

import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.가격제한폭확대Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.kospiFuture.K200선물_가격제한폭확대;

public class K200선물_가격제한폭확대Feed extends Feed implements 가격제한폭확대Feed {

    public K200선물_가격제한폭확대Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    public byte[] getCodeByte() {
        return K200선물_가격제한폭확대.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    @Override
    public String getRepresentingCode() {
        return getCode();
    }

    @Override
    public Integer get상한가() {
        return K200선물_가격제한폭확대.상한가.parser().parseIntWithLeadingSign(rawPacket);
    }

    @Override
    public Integer get하한가() {
        return K200선물_가격제한폭확대.하한가.parser().parseIntWithLeadingSign(rawPacket);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        updateLimitPriceAndQuoter();
    }
}
