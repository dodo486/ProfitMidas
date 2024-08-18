package bulls.feed.listen;

import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.test.KrxPollingFeedLogCenter;
import bulls.feed.udpInfo.RawFeedInfoForSocket;
import bulls.server.enums.ServerLocation;
import bulls.staticData.TempConf;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

public class FeedProducer {
    private final RingBuffer<ConcreteFeed> ringBuffer;
    private final String feedIfIp;

    public FeedProducer(RingBuffer<ConcreteFeed> ringBuffer, String feedIfIp) {
        this.ringBuffer = ringBuffer;
        this.feedIfIp = feedIfIp;
    }


    public void onData(ByteBuffer buf, RawFeedInfoForSocket feedInfoForSocket) {
        byte[] byteReceived = new byte[buf.limit()];
        buf.flip();
        buf.get(byteReceived, 0, buf.limit());
        onData(byteReceived, feedInfoForSocket);
    }

    public void onData(byte[] byteReceived, RawFeedInfoForSocket feedInfoForSocket) {
        FeedTRCode feedTRCode;
        if (feedInfoForSocket.getLocation() == ServerLocation.SEOUL) {
            feedTRCode = FeedTRCode.matchTR(byteReceived);
        } else {
            feedTRCode = FeedTRCode.matchTRPusan(byteReceived);
        }
        if (!feedInfoForSocket.isValidTRCode(feedTRCode)) {
            return;
        }
        if (TempConf.LOG_POLLING_FEED)
            KrxPollingFeedLogCenter.Instance.increaseCountIfPolling(feedIfIp, feedInfoForSocket, new String(byteReceived));
        long sequence = ringBuffer.next();  // Grab the next sequence
        try {
            ConcreteFeed feed = ringBuffer.get(sequence); // Get the entry in the Disruptor
            // for the sequence
            feed.of(byteReceived, feedInfoForSocket, feedTRCode);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
