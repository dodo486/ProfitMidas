package bulls.analysis;

import bulls.data.PriceInfo;
import bulls.data.bidAsk.BidAskCore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AnalysisUtil {

    private static final Map<String, Integer> contractAmountSumMap = new HashMap<>();

    public static int bidAskBinarySearch(List<? extends BidAskCore> bidAskList, LocalTime t) {
        long timeLong = getLongFromNanoTime(t);

        if (bidAskList.size() == 0)
            return 0;
        else if (timeLong < bidAskList.get(0).feedStamp)
            return 0;
        else if (timeLong > bidAskList.get(bidAskList.size() - 1).feedStamp)
            return bidAskList.size() - 1;

        int left = 0;
        int right = bidAskList.size() - 1;
        int middle;
        long value;

        while (true) {
            middle = (int) Math.floor((left + right) / 2);
            value = bidAskList.get(middle).feedStamp;

            if (value == timeLong) {
                return middle;
            } else if (value < timeLong) {
                if (left == middle) {
                    if (bidAskList.get(right).feedStamp <= timeLong)
                        return right;
                    else
                        return left;
                }
                left = middle;
            } else {
                if (right == middle) {
                    return right;
                }
                right = middle;
            }
        }
    }

    public static int priceInfoBinarySearch(List<PriceInfo> priceInfoList, LocalTime t) {
        long timeLong = getLongFromNanoTime(t);

        if (priceInfoList.size() == 0)
            return 0;
        else if (timeLong < priceInfoList.get(0).feedStamp)
            return 0;
        else if (timeLong > priceInfoList.get(priceInfoList.size() - 1).feedStamp)
            return priceInfoList.size() - 1;

        int left = 0;
        int right = priceInfoList.size() - 1;
        int middle;
        long value;

        while (true) {
            middle = (int) Math.floor((left + right) / 2);
            value = priceInfoList.get(middle).feedStamp;

            if (value == timeLong) {
                return middle;
            } else if (value < timeLong) {
                if (left == middle) {
                    if (priceInfoList.get(right).feedStamp <= timeLong)
                        return right;
                    else
                        return left;
                }
                left = middle;
            } else {
                if (right == middle) {
                    return right;
                }
                right = middle;
            }
        }
    }

    public static LocalTime getTimeByContractAmountPercent(List<PriceInfo> priceInfoList, LocalTime t, int percent) {
        String isinCode = priceInfoList.get(0).isinCode;
        int contractAmountSum;

        if (contractAmountSumMap.containsKey(isinCode)) {
            contractAmountSum = contractAmountSumMap.get(isinCode);
        } else {
            contractAmountSum = priceInfoList.stream().mapToInt(info -> info.amount).sum();
            contractAmountSumMap.put(isinCode, contractAmountSum);
        }

        contractAmountSum = (contractAmountSum * percent) / 100;

        int amountSum = 0;
        int i = 0;

        for (i = priceInfoBinarySearch(priceInfoList, t); i < priceInfoList.size() && amountSum < contractAmountSum; i++) {
            amountSum += priceInfoList.get(i).amount;
        }

        if (i >= priceInfoList.size())
            return getLocalTimeFromNanoLong(priceInfoList.get(priceInfoList.size() - 1).feedStamp);

        return getLocalTimeFromNanoLong(priceInfoList.get(i).feedStamp);
    }

    public static LocalTime getLocalTimeFromBytes(byte[] timeBytes) {
        long time = Long.parseLong(new String(timeBytes));

        if (time < 1000000000L) { // millTime
            time *= 1000000L;
        }

        return getLocalTimeFromNanoLong(time);
    }

    public static long getLongFromNanoTime(LocalTime t) {
        return ((t.getHour() * 100 + t.getMinute()) * 100 + t.getSecond()) * 1000000000L + t.getNano();
    }

    public static long getTruncatedLongFromNanoTime(LocalTime t) {
        return (((t.getHour() * 100 + t.getMinute()) * 100 + t.getSecond()) * 1000000000L + t.getNano()) / 1000000L;
    }

    public static LocalTime getLocalTimeFromNanoLong(long nanoTime) {
        int hour, min, sec, nanoSec;

        nanoSec = (int) (nanoTime % 1000000000L);
        nanoTime /= 1000000000L;
        sec = (int) (nanoTime % 100L);
        nanoTime /= 100L;
        min = (int) (nanoTime % 100L);
        nanoTime /= 100L;
        hour = (int) nanoTime;

        return LocalTime.of(hour, min, sec, nanoSec);
    }

    public static void applySyncTimingToBidAskList(List<? extends BidAskCore> bidAskList, double syncTiming) {
        long timing = Math.round(syncTiming);

        for (var bidAsk : bidAskList) {
            bidAsk.feedStamp = AnalysisUtil.getLongFromNanoTime(AnalysisUtil.getLocalTimeFromNanoLong(bidAsk.feedStamp).minusNanos(timing));
        }
    }

    public static LocalDate getLocalDateFromDateString(String dateString) {
        if (dateString.length() != 8)
            return null;

        try {
            int year = Integer.parseInt(dateString.substring(0, 4));
            int month = Integer.parseInt(dateString.substring(4, 6));
            int day = Integer.parseInt(dateString.substring(6, 8));

            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            return null;
        }
    }
}
