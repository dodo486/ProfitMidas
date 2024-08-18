package bulls.data.bidAsk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import bulls.data.PriceInfo;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DocumentConvertible;
import bulls.exception.NoBidAskDataException;
import bulls.staticData.UpDown;
import bulls.staticData.tick.TickCalculatorCenter;
import bulls.staticData.tick.TickFunction;
import org.bson.Document;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class BidAskCore implements DocumentConvertible {
    public final String trCode = "BidAskInfo"; // for json communication
    public String isinCode;
    public int[] bidAmount = new int[10];
    public int[] askAmount = new int[10];
    public int[] bidPrice = new int[10];
    public int[] askPrice = new int[10];
    public int maxBidAskDepth = 10;
    public BidAskState baState = BidAskState.FULL;
    public long feedStamp = 0; // 시세 도착 시간
    public int prevPrice = 0;
    public long accVolume = 0; // 장 상태(보드 ID) 구분 없이 현재까지의 총 누적 체결 from 호가Feed

    // 예상 체결가와 예상체결 수량이 둘다 0이면 데이터를 쓸 수 없다고 판단
    public long expAmt = 0;
    public long expPrice = 0;

    public transient PriceInfo priceInfo = null;

    public double getMid() {
        if (bidPrice[0] == 0)
            return askPrice[0];
        if (askPrice[0] == 0)
            return bidPrice[0];
        return (bidPrice[0] + askPrice[0]) / 2.0;
    }

    @JsonIgnore
    public int getBestBid() throws NoBidAskDataException {
        if (bidAmount[0] <= 0)
            throw new NoBidAskDataException(" Bid 호가가 없습니다", isinCode);
        return bidPrice[0];
    }

    @JsonIgnore
    public int getBestAsk() throws NoBidAskDataException {
        if (askAmount[0] <= 0)
            throw new NoBidAskDataException(" Ask 호가가 없습니다", isinCode);
        return askPrice[0];
    }

    public int getBidAskSpread() {
        if (baState != BidAskState.FULL)
            return Integer.MAX_VALUE;
        return askPrice[0] - bidPrice[0];
    }

    public int getBidIndexOfPrice(int price) {
        for (int i = 0; i < bidPrice.length; ++i) {
            if (bidAmount[i] > 0) {
                if (bidPrice[i] == price) {
                    return i;
                }
            } else {
                break;
            }
        }
        return -1; //호가 안보임
    }

    public int getBidAmountOfPrice(int price) {
        int minPrice = Integer.MAX_VALUE;
        if (bidAmount[0] > 0) {
            if (bidPrice[0] == price)
                return bidAmount[0];
            if (bidPrice[0] < price)
                return 0;
        }
        int i;
        for (i = 1; i < maxBidAskDepth; ++i) {
            if (bidAmount[i] > 0) {
                minPrice = bidPrice[i];
                if (bidPrice[i] == price) {
                    return bidAmount[i];
                }
            } else {
                break;
            }
        }
        if (price < minPrice && maxBidAskDepth == i - 1)
            return -1; //호가 안보임
        else
            return 0; // 현재 보이는 호가의 최소값보다 높은 가격의 호가이고 앞에서 발견되지 않았따면 수량이 0이라고 할 수 있음.
    }

    public int getAskIndexOfPrice(int price) {
        for (int i = 0; i < askPrice.length; ++i) {
            if (askAmount[i] > 0) {
                if (askPrice[i] == price) {
                    return i;
                }
            } else {
                break;
            }
        }
        return -1; //호가 안보임
    }

    public int getAskAmountOfPrice(int price) {
        int maxPrice = Integer.MIN_VALUE;
        if (askAmount[0] > 0) {
            if (askPrice[0] == price)
                return askAmount[0];
            if (askPrice[0] > price)
                return 0;
        }
        int i;
        for (i = 1; i < maxBidAskDepth; ++i) {
            if (askAmount[i] > 0) {
                maxPrice = askPrice[i];
                if (askPrice[i] == price) {
                    return askAmount[i];
                }
            } else {
                break;
            }
        }
        if (price > maxPrice && maxBidAskDepth == i - 1)
            return -1; //호가 안보임
        else
            return 0; // 현재 보이는 호가의 최소값보다 높은 가격의 호가이고 앞에서 발견되지 않았따면 수량이 0이라고 할 수 있음.
    }


    public double getPrevDiffPcnt() {
        if (prevPrice == 0)
            return 0;
        else {
            if (bidPrice[0] != 0 && askPrice[0] != 0) {
                return (bidPrice[0] + askPrice[0]) / 2.0 / (double) prevPrice * 100.0 - 100.0;
            } else if (bidPrice[0] != 0 && askPrice[0] == 0) {
                return (bidPrice[0]) / (double) prevPrice * 100.0 - 100.0;
            } else if (bidPrice[0] == 0 && askPrice[0] != 0) {
                return (askPrice[0]) / (double) prevPrice * 100.0 - 100.0;
            } else {
                return 0;
            }
        }
    }

    public int getPrevPrice() {
        return this.prevPrice;
    }

    public void setPrevPrice(int price) {
        this.prevPrice = price;
    }

    public String printWith(int bid, int ask) {

        StringBuilder sb = new StringBuilder();
        sb.append("========= ");
        sb.append(isinCode);
        sb.append(" BidAsk");
        sb.append(" =========\n");
        for (int i = 5; i > 0; i--) {
            if (askPrice[i - 1] == ask)
                sb.append(String.format("V  %5s     %7s               ", askAmount[i - 1], askPrice[i - 1]));
            else if (askPrice[i - 1] == bid)
                sb.append(String.format("   %5s     %7s              V", askAmount[i - 1], askPrice[i - 1]));
            else
                sb.append(String.format("   %5s     %7s               ", askAmount[i - 1], askPrice[i - 1]));
            sb.append("\n");
        }
        for (int i = 0; i < 5; i++) {
            if (bidPrice[i] == bid)
                sb.append(String.format("             %7s     %7s  V", bidPrice[i], bidAmount[i]));
            else if (bidPrice[i] == ask)
                sb.append(String.format("V            %7s     %7s   ", bidPrice[i], bidAmount[i]));
            else
                sb.append(String.format("             %7s     %7s   ", bidPrice[i], bidAmount[i]));
            sb.append("\n");
        }
        return sb.toString();
    }


    public boolean isTightBidAsk() {
        TickFunction tickFunc = TickCalculatorCenter.Instance.getPriceTickFunction(isinCode);
        return tickFunc.getPriceByTick(UpDown.UP, bidPrice[0], 1) == askPrice[0];
    }

    public int spreadTickCount(TickFunction tickFunction) {
        return tickFunction.getTickCountBetween(askPrice[0], bidPrice[0]);
    }

    public int getMaxBidAskDepth() {
        return maxBidAskDepth;
    }

    public boolean isLowestBid(int depth) {
        return depth == maxBidAskDepth - 1 || bidAmount[depth + 1] == 0;
    }

    public boolean isHighestAsk(int depth) {
        return depth == maxBidAskDepth - 1 || askAmount[depth + 1] == 0;
    }

    public void updateBidAskState() {
        if (bidAmount[0] == 0) {
            if (askAmount[0] == 0) {
                baState = BidAskState.EMPTY;
            } else {
                baState = BidAskState.ASK_ONLY;
            }
        } else {
            if (askAmount[0] == 0) {
                baState = BidAskState.BID_ONLY;
            } else {
                baState = BidAskState.FULL;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========= ");
        sb.append(isinCode);
        sb.append(" BidAsk volume=");
        sb.append(accVolume);
        sb.append(" state=");
        sb.append(baState);
        sb.append(" =========\n");
        for (int i = 5; i > 0; i--) {
            sb.append(String.format("%5s     %7s", askAmount[i - 1], askPrice[i - 1]));
            sb.append("\n");
        }
        for (int i = 0; i < 5; i++) {
            sb.append(String.format("          %7s     %7s", bidPrice[i], bidAmount[i]));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Document getDataDocument() {
        Document doc = new Document();
        doc.put("isinCode", isinCode);
        doc.put("dt", TimeCenter.Instance.getDateFromFeedTimestamp(feedStamp));
        doc.put("volume", accVolume);
        doc.put("prevPrice", prevPrice);
        doc.put("maxBidAskDepth", maxBidAskDepth);
        doc.put("bidPrice", Arrays.stream(bidPrice).boxed().collect(Collectors.toList()));
        doc.put("bidAmount", Arrays.stream(bidAmount).boxed().collect(Collectors.toList()));
        doc.put("askPrice", Arrays.stream(askPrice).boxed().collect(Collectors.toList()));
        doc.put("askAmount", Arrays.stream(askAmount).boxed().collect(Collectors.toList()));
        return doc;
    }

    @Override
    public Document getQueryDocument() {
        Document doc = new Document();
        doc.put("isinCode", isinCode);
        return doc;
    }
}
