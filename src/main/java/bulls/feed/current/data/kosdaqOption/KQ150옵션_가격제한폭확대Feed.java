package bulls.feed.current.data.kosdaqOption;

import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.가격제한폭확대Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.kospiOption.KQ150옵션_가격제한폭확대;

public class KQ150옵션_가격제한폭확대Feed extends Feed implements 가격제한폭확대Feed {

    public KQ150옵션_가격제한폭확대Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    public byte[] getCodeByte() {
        return KQ150옵션_가격제한폭확대.isinCode.parser().parseByte(rawPacket);
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
        return KQ150옵션_가격제한폭확대.상한가.parser().parseInt(rawPacket);
    }

    @Override
    public Integer get하한가() {
        return KQ150옵션_가격제한폭확대.하한가.parser().parseInt(rawPacket);
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        updateLimitPriceAndQuoter();
    }
}
