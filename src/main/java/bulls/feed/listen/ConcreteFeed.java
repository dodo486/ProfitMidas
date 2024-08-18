package bulls.feed.listen;

import bulls.feed.abstraction.Feed;
import bulls.feed.abstraction.FeedFactory;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.udpInfo.RawFeedInfoForSocket;
import bulls.server.enums.ServerLocation;
import bulls.staticData.TempConf;
import bulls.tool.util.tracer.ElapsedTimeTracer;

import java.nio.ByteBuffer;

public class ConcreteFeed {

    public ConcreteFeed() {

    }

    Feed feed;

    public void of(ByteBuffer buf, RawFeedInfoForSocket feedInfoForSocket) {

        long feedStamp = System.nanoTime();
        byte[] byteReceived = new byte[buf.limit()];
        buf.flip();
        buf.get(byteReceived, 0, buf.limit());
        ElapsedTimeTracer tracer = ElapsedTimeTracer.createTT(TempConf.REMAIN_ORDER_ELAPSEDTIME, feedStamp);
        FeedTRCode trCode;

        if (feedInfoForSocket.getLocation() == ServerLocation.SEOUL) {
            trCode = FeedTRCode.matchTR(byteReceived);
            if (feedInfoForSocket.isValidTRCode(trCode))
                feed = FeedFactory.createFeedFromHeuristic(trCode, byteReceived, feedStamp);
            else
                feed = FeedFactory.notAllowed();
        } else {
            trCode = FeedTRCode.matchTRPusan(byteReceived);
            if (feedInfoForSocket.isValidTRCode(trCode))
                feed = FeedFactory.createPusanFeed(trCode, byteReceived, feedStamp);
            else
                feed = FeedFactory.notAllowed();
        }
        feed.tracer = tracer;
    }

    public void of(byte[] packet, RawFeedInfoForSocket feedInfoForSocket) {
        long feedStamp = System.nanoTime();
        FeedTRCode trCode;
        ElapsedTimeTracer tracer = ElapsedTimeTracer.createTT(TempConf.REMAIN_ORDER_ELAPSEDTIME, feedStamp);
        if (feedInfoForSocket.getLocation() == ServerLocation.SEOUL) {
            trCode = FeedTRCode.matchTR(packet);
            if (feedInfoForSocket.isValidTRCode(trCode))
                feed = FeedFactory.createFeedFromHeuristic(trCode, packet, feedStamp);
            else
                feed = FeedFactory.notAllowed();
        } else {
            trCode = FeedTRCode.matchTRPusan(packet);
            if (feedInfoForSocket.isValidTRCode(trCode))
                feed = FeedFactory.createPusanFeed(trCode, packet, feedStamp);
            else
                feed = FeedFactory.notAllowed();
        }

        feed.tracer = tracer;
    }

    /**
     * feed match와 valid여부가 이미 확인 되었다고 가정한 상태에서 호출되는 메소드
     *
     * @param packet
     * @param feedInfoForSocket
     * @param trCode
     */
    public void of(byte[] packet, RawFeedInfoForSocket feedInfoForSocket, FeedTRCode trCode) {
        long feedStamp = System.nanoTime();
        ElapsedTimeTracer tracer = ElapsedTimeTracer.createTT(TempConf.REMAIN_ORDER_ELAPSEDTIME, feedStamp);
        if (feedInfoForSocket.getLocation() == ServerLocation.SEOUL) {
            feed = FeedFactory.createFeedFromHeuristic(trCode, packet, feedStamp);
        } else {
            feed = FeedFactory.createPusanFeed(trCode, packet, feedStamp);
        }
        feed.tracer = tracer;
    }

    public Feed getFeed() {
        return feed;
    }
}
