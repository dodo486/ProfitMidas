package bulls.db.influxdb;

import bulls.data.bidAsk.BidAskCore;
import bulls.data.bidAsk.NativeBidAskGenerator;
import bulls.dateTime.TimeCenter;
import bulls.feed.abstraction.종목정보Feed;
import bulls.feed.abstraction.체결Feed;
import bulls.feed.abstraction.호가Feed;
import bulls.order.enums.LongShort;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public interface InfluxDBData {
    default String getIsinCode() {
        if (this instanceof 체결Feed) {
            return ((체결Feed) this).getRepresentingCode();
        }
        if (this instanceof 호가Feed) {
            return ((호가Feed) this).getRepresentingCode();
        }
        if (this instanceof 종목정보Feed) {
            return ((종목정보Feed) this).getRepresentingCode();
        }

        return null;
    }

    default String getBoardId() {
        if (this instanceof 체결Feed) {
            return ((체결Feed) this).getBoardId();
        }
        if (this instanceof 호가Feed) {
            return ((호가Feed) this).getBoardId();
        }

        return null;
    }

    default List<Point> toPoints() {
        ArrayList<Point> points = new ArrayList<>();

        if (this instanceof 체결Feed) {
            points.add(InfluxDBData.toTradePoint((체결Feed) this));
        }
        if (this instanceof 호가Feed) {
            Point point = InfluxDBData.toOrderBookPoint((호가Feed) this);
            if (point != null) { // 선물 또는 옵션 단일가 거래시에 호가 0으로 주는 경우는 저장하지 않음
                points.add(point);
            }
        }

        return points;
    }

    static Point toTradePoint(체결Feed feed) {
        return Point
                .measurement("trade")
                .addTag("isinCode", feed.getRepresentingCode())
                .addField("price", feed.getPrice())
                .addField("amount", feed.getAmount())
                .addField("money", (long) feed.getPrice() * feed.getAmount())
                .addField("ls", feed.getLongShort() == LongShort.LONG ? "L" : (feed.getLongShort() == LongShort.SHORT ? "S" : "U"))
                .time(TimeCenter.Instance.getLocalDateTimeFromFeedTimestamp(feed.getArrivalStamp()).atZone(ZoneId.systemDefault()).toInstant(), WritePrecision.NS);
    }

    static Point toOrderBookPoint(호가Feed feed) {
        BidAskCore ba = feed.getFullBidAsk(NativeBidAskGenerator.Instance);

        if (ba.bidPrice[0] == 0 && ba.askPrice[0] == 0) {
            return null;
        }

        return Point
                .measurement("orderBook")
                .addTag("isinCode", ba.isinCode)
                .addField("bidPrice1", ba.bidPrice[0])
                .addField("bidAmount1", ba.bidAmount[0])
                .addField("bidPrice2", ba.bidPrice[1])
                .addField("bidAmount2", ba.bidAmount[1])
                .addField("bidPrice3", ba.bidPrice[2])
                .addField("bidAmount3", ba.bidAmount[2])
                .addField("bidPrice4", ba.bidPrice[3])
                .addField("bidAmount4", ba.bidAmount[3])
                .addField("bidPrice5", ba.bidPrice[4])
                .addField("bidAmount5", ba.bidAmount[4])
                .addField("askPrice1", ba.askPrice[0])
                .addField("askAmount1", ba.askAmount[0])
                .addField("askPrice2", ba.askPrice[1])
                .addField("askAmount2", ba.askAmount[1])
                .addField("askPrice3", ba.askPrice[2])
                .addField("askAmount3", ba.askAmount[2])
                .addField("askPrice4", ba.askPrice[3])
                .addField("askAmount4", ba.askAmount[3])
                .addField("askPrice5", ba.askPrice[4])
                .addField("askAmount5", ba.askAmount[4])
                .addField("totalBidAmount", IntStream.of(ba.bidAmount).sum())
                .addField("totalAskAmount", IntStream.of(ba.askAmount).sum())
                .addField("acc_vol", ba.accVolume)
                .time(TimeCenter.Instance.getLocalDateTimeFromFeedTimestamp(feed.getArrivalStamp()).atZone(ZoneId.systemDefault()).toInstant(), WritePrecision.NS);
    }
}
