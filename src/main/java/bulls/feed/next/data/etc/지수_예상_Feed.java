package bulls.feed.next.data.etc;

import bulls.data.bidAsk.BidAskCore;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.etc.지수;
import bulls.staticData.IndexCode;

public class 지수_예상_Feed extends Feed {

    public static byte[] timeEnd = {'J', 'U', 'N', 'J', 'J', 'J'};

    public 지수_예상_Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }


    // 지수의 경우는 단일 가격으로 나오지만 전략 적용의 편의를 위해 bid/ask 에 같은 단일 가격을 업데이트한다.
    // 수량은 만일의 경우의 null protection 과 divide by 0 을 막기 위해 일단 1로 셋팅.. 안쓸거다.. 쓰지 마라.
    //거래량과 거래대금을 각각 bid수량1, ask수량1에 넣어주고 있음.
    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        BidAskCore ba = dc.getBidAskFactory().getBidAsk(ETC_MAX_BIDASK_DEPTH);

        String code = getIndexCode();
        IndexCode ic = IndexCode.parseExpectedPriceFeedTRWithIndustryCode(code);
        if (ic == null)
            return;

        ba.isinCode = ic.getIndexIsinCode();
        ba.askPrice[0] = getAskPrice();
        ba.bidPrice[0] = getBidPrice();
        ba.bidAmount[0] = getVolume();
        ba.askAmount[0] = getMoney();
        ba.updateBidAskState();
        dc.updateEquityBidAskMap(ba.isinCode, ba);
    }

    public String getIndexCode() {
        return new String(지수.indexCode.parser().parseByte(rawPacket));
    }

    public Integer getAskPrice() {
        return 지수.index.parser().parseInt(rawPacket);
    }

    public Integer getBidPrice() {
        return 지수.index.parser().parseInt(rawPacket);
    }

    public Integer getVolume() {
        return 지수.volume.parser().parseInt(rawPacket);
    }

    public Integer getMoney() {
        return 지수.money.parser().parseInt(rawPacket);
    }


}
