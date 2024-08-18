package bulls.feed.abstraction;

import bulls.exception.UnidentifiedTrCodeException;
import bulls.feed.current.data.equity.Equity_체결Feed;
import bulls.feed.current.data.equity.Equity_호가Feed;
import bulls.feed.current.data.etc.FeedNotAllowed;
import bulls.feed.current.data.etc.UndefinedFeed;
import bulls.feed.current.data.etc.지수_Feed;
import bulls.feed.current.data.etf.ETF_호가Feed;
import bulls.feed.current.data.pusan.*;
import bulls.feed.current.enums.FeedTRCode;
import bulls.log.DefaultLogger;
import bulls.server.enums.ServerLocation;

import java.lang.reflect.InvocationTargetException;

public abstract class FeedFactory {

    private static final FeedNotAllowed NOT_ALLOWED = new FeedNotAllowed();

    public static Feed createFeedFromReflection(byte[] packet) {
        FeedTRCode trCode = FeedTRCode.matchTR(packet);

        Feed feed = null;
        try {
            feed = trCode.getConstructor().newInstance(trCode, packet);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            DefaultLogger.logger.error("error found", e);
        }
        if (feed == null) {
            try {
                feed = new UndefinedFeed(trCode, packet);
            } catch (UnidentifiedTrCodeException e) {
                DefaultLogger.logger.error("error found", e);
            }

        }
        return feed;
    }

    public static Feed notAllowed() {
        return NOT_ALLOWED;
    }

    public static Feed createFeedFromHeuristic(ServerLocation feedLocation, byte[] packet, long feedStamp) {
        FeedTRCode trCode;

        if (feedLocation == ServerLocation.SEOUL)
            trCode = FeedTRCode.matchTR(packet);
        else
            trCode = FeedTRCode.matchTRPusan(packet);

        Feed feed = null;

        if (feedLocation == ServerLocation.SEOUL) {
            if (trCode.equals(FeedTRCode.B6011)) {
                feed = new Equity_호가Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.A3011)) {
                feed = new Equity_체결Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.B7011)) {
                feed = new ETF_호가Feed(trCode, packet);
            }
        } else {
            // hack... 부산 시세와 서울 시세의 코드가 같기 때문에 우선 부산 코드부터 필터링...
            // 부산 시세는 reflection 으로 feed 생성 불가능
            if (trCode.equals(FeedTRCode.P_B6014)) {
                feed = new PusanFuture_호가Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.P_G7014)) {
                feed = new PusanFuture_체결호가Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.P_A3014)) {
                feed = new PusanFuture_체결Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.P_B6034)) {
                feed = new PusanOption_호가Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.P_G7034)) {
                feed = new PusanOption_체결호가Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.P_A3034)) {
                feed = new PusanOption_체결Feed(trCode, packet);
            }
            // 미니 선물
            else if (trCode.equals(FeedTRCode.P_B6124)) {
                feed = new PusanFuture_호가Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.P_G7124)) {
                feed = new PusanFuture_체결호가Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.P_A3124)) {
                feed = new PusanFuture_체결Feed(trCode, packet);
            }
            //미니 옵션
            else if (trCode.equals(FeedTRCode.P_B6134)) {
                feed = new PusanOption_호가Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.P_G7134)) {
                feed = new PusanOption_체결호가Feed(trCode, packet);
            } else if (trCode.equals(FeedTRCode.P_A3134)) {
                feed = new PusanOption_체결Feed(trCode, packet);
            }
        }

        if (feed != null) {
            feed.arrivalStamp = feedStamp;
            return feed;
        }

        // 지수
        if (trCode.equals(FeedTRCode.D2011) || trCode.equals(FeedTRCode.N5011) || trCode.equals(FeedTRCode.T9012)) {
            feed = new 지수_Feed(trCode, packet);
        } else {
            feed = createFeedFromReflection(packet);
        }
        feed.arrivalStamp = feedStamp;
        return feed;
    }

    public static Feed createFeedFromHeuristic(FeedTRCode trCode, byte[] packet, long feedStamp) {

        Feed feed = null;

        if (trCode.equals(FeedTRCode.B6011)) {
            feed = new Equity_호가Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.A3011)) {
            feed = new Equity_체결Feed(trCode, packet);
        }

        if (feed != null) {
            feed.arrivalStamp = feedStamp;
            return feed;
        }

        // 지수
        if (trCode.equals(FeedTRCode.D2011) || trCode.equals(FeedTRCode.N5011) || trCode.equals(FeedTRCode.T9012)) {
            feed = new 지수_Feed(trCode, packet);
        } else {
            feed = createFeedFromReflection(packet);
        }
        feed.arrivalStamp = feedStamp;
        return feed;
    }

    public static Feed createPusanFeed(FeedTRCode trCode, byte[] packet, long feedStamp) {

        Feed feed = null;

        // hack... 부산 시세와 서울 시세의 코드가 같기 때문에 부산 시세는 reflection 으로 feed 생성 하지 않는다
        if (trCode.equals(FeedTRCode.P_B6014)) {
            feed = new PusanFuture_호가Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.P_G7014)) {
            feed = new PusanFuture_체결호가Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.P_A3014)) {
            feed = new PusanFuture_체결Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.P_B6034)) {
            feed = new PusanOption_호가Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.P_G7034)) {
            feed = new PusanOption_체결호가Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.P_A3034)) {
            feed = new PusanOption_체결Feed(trCode, packet);
        }
        // 미니 선물
        else if (trCode.equals(FeedTRCode.P_B6124)) {
            feed = new PusanFuture_호가Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.P_G7124)) {
            feed = new PusanFuture_체결호가Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.P_A3124)) {
            feed = new PusanFuture_체결Feed(trCode, packet);
        }
        //미니 옵션
        else if (trCode.equals(FeedTRCode.P_B6134)) {
            feed = new PusanOption_호가Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.P_G7134)) {
            feed = new PusanOption_체결호가Feed(trCode, packet);
        } else if (trCode.equals(FeedTRCode.P_A3134)) {
            feed = new PusanOption_체결Feed(trCode, packet);
        }


        if (feed != null) {
            feed.arrivalStamp = feedStamp;
            return feed;
        }

        return NOT_ALLOWED;
    }
}

