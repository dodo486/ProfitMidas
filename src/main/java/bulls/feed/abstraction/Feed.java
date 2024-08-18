package bulls.feed.abstraction;

import bulls.annotation.Explanation;
import bulls.feed.current.enums.FeedTRCode;
import bulls.tool.util.tracer.ElapsedTimeTracer;

@Explanation
/**
 *
 * Feed 클래스 자체는 시세 Observer 패턴의 data 역활을 하며 UDP로 받는 패킷 하나에 1:1 대응하는 개념
 * 파싱의 시발점은 FeedTRCode enum , 각 TrCode 별 파싱 클래스정보, 해당 Tr의 Feed 정보를 가지고 있다.
 * 거래소 TRCode 별로 enum 클래스로 파싱 데이터를 할당. (예 : Equity_체결, Future_종가 등)
 * 예전의 getResourceAsStream 을 통해 파싱 정보를 받고 한 클래스에서 파싱을 처리할 경우 코드 중복이 없긴 하나 추후 디버깅 상황 발생시
 * 데이터 내용을 일일이 뜯어봐야하는 불편함이 있고 코딩시 실수의 여지가 매우 크다.
 * 그리고 무엇보다 String 을 키값으로 매번 Hash 맵에 억세스 하는것에 비해서 퍼포먼스가 낫다.
 * 특히 TR에 없는 키를 조회하는 버그같은건 런타임 전에는 티도 안나는 부분이라 우연히 비슷한 키값으로 파싱했다가 런타임 문제 발생하면 그대로 재앙
 * 코드 재사용성이나 패턴 적용의 여지를 해쳐서 좀 답답하더라도 일단 일일이 enum 으로 구현함.
 */

public abstract class Feed {
    protected final int ETC_MAX_BIDASK_DEPTH = 1;
    protected final int EQUITY_MAX_BIDASK_DEPTH = 10;
    protected final int DERIV_MAX_BIDASK_DEPTH = 5;
    protected final int DERIV_STOCK_MAX_BIDASK_DEPTH = 10;
    protected final FeedTRCode trCode;
    protected final byte[] rawPacket;

    public long arrivalStamp = 0;
    public ElapsedTimeTracer tracer;

    public FeedTRCode getTrCode() {
        return trCode;
    }

    public byte[] getRawPacket() {
        return rawPacket;
    }

    public Feed(FeedTRCode trCode, byte[] packet) {
        rawPacket = new byte[packet.length];
        System.arraycopy(packet, 0, rawPacket, 0, packet.length);
        this.trCode = trCode;
    }

    public abstract void updateBidAskFill(BidAskFillUpdater dc);
}

