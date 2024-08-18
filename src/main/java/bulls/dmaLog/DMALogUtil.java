package bulls.dmaLog;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DMALogUtil {

    private static final int[] nanoSecCorrection = {0,
            100_000_000,
            10_000_000,
            1_000_000,
            100_000,
            10_000,
            1_000,
            100,
            10,
            1};

    public static LocalTime parseTimeInLog(String logString) {
        Pattern p = Pattern.compile("([0-1][0-9]|2[0-4])([0-5][0-9]){2}\\.[0-9]{9}");
        Matcher m = p.matcher(logString);

        if (m.find()) {
            String rawTimeString = m.group(0);
            int hour, min, sec, nanoSec;

            hour = Integer.parseInt(rawTimeString.substring(0, 2));
            min = Integer.parseInt(rawTimeString.substring(2, 4));
            sec = Integer.parseInt(rawTimeString.substring(4, 6));
            nanoSec = Integer.parseInt(rawTimeString.substring(7, 16)) * nanoSecCorrection[16 - 7];

            return LocalTime.of(hour, min, sec, nanoSec);
        } else {
            System.out.println("DMALog - Time Parsing Error");
            return LocalTime.MIN;
        }
    }

    public static LocalTime parseTimeInPcap(String logString) {
        boolean isNanoTime = logString.length() == 15;

        int hour, min, sec, nanoSec;
        hour = Integer.parseInt(logString.substring(0, 2));
        min = Integer.parseInt(logString.substring(2, 4));
        sec = Integer.parseInt(logString.substring(4, 6));

        if (isNanoTime)
            nanoSec = Integer.parseInt(logString.substring(6, 15)) * nanoSecCorrection[15 - 6];
        else
            nanoSec = Integer.parseInt(logString.substring(6, 9)) * nanoSecCorrection[9 - 6];

        return LocalTime.of(hour, min, sec, nanoSec);
    }

    public static String parsePacket(String logString) {
        try {
            byte[] packetBytes = logString.getBytes();
            int i = 0;
            while (true) {
                if (packetBytes[i] == 'T' && packetBytes[i + 1] == 'C' && packetBytes[i + 2] == 'P')
                    break;
                i++;
            }

            i += 7;
            int start = i;

            while (true) {
                if (packetBytes[i] == ']')
                    break;
                i++;
            }

            int end = i - 1;

            return new String(packetBytes, start, end - start + 1);
            //return logString.split("TCP")[1].substring(4).split("]")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    public static LocalTime convertKRXTime(String krxTime) {
        if (krxTime.length() != 9)
            return LocalTime.MIN;

        try {
            int hour, min, sec, nanoSec;

            hour = Integer.parseInt(krxTime.substring(0, 2));
            min = Integer.parseInt(krxTime.substring(2, 4));
            sec = Integer.parseInt(krxTime.substring(4, 6));
            nanoSec = Integer.parseInt(krxTime.substring(6, 9)) * nanoSecCorrection[9 - 6];

            return LocalTime.of(hour, min, sec, nanoSec);
        } catch (NumberFormatException e) {
            return LocalTime.MIN;
        }
    }

    public static Map<String, List<TradeDMALog>> convertToTradeLogMap(Map<String, ConcurrentLinkedQueue<DMALog>> logMap, String keyName) {
        Map<String, List<TradeDMALog>> fillLogMap = new HashMap<>();

        for (String orderId : logMap.keySet()) {
            ConcurrentLinkedQueue<DMALog> orderLogList = logMap.get(orderId);

            for (DMALog log : orderLogList) {
                if (log instanceof TradeDMALog) {
                    String key;
                    if (keyName.equals("isinCode"))
                        key = log.getIsinCode();
                    else if (keyName.equals("bookCode"))
                        key = log.getBookCode();
                    else if (keyName.equals("account"))
                        key = log.getAccountNumber();
                    else {
                        System.out.println("convertToTradeLogMap Error : Key name is not valid (" + keyName + ")");
                        return new HashMap<>();
                    }

                    fillLogMap.computeIfAbsent(key, k -> new ArrayList<>()).add((TradeDMALog) log);
                }
            }
        }

        return fillLogMap;
    }

    // ls_ts_sm204 -> serverType_sendType_masterType|serverNumberString
    public static List<String> parseTypeString(String logString) {
        List<String> result = new ArrayList<>();
        String[] typeString = logString.split("\\|")[1].split("_");

        if (typeString.length != 3)
            return result;

        result.add(typeString[0]);
        result.add(typeString[1]);

        for (int i = 0; i < typeString[2].length(); i++) {
            if (Character.isDigit(typeString[2].charAt(i))) {
                result.add(typeString[2].substring(0, i));
                result.add(typeString[2].substring(i));
                break;
            }
        }
        return result;
    }

    public static List<DMALog> timeFilter(List<DMALog> logList, LocalTime startTime, LocalTime endTime, int count) {
        if (startTime == null)
            startTime = LocalTime.MIN;
        if (endTime == null)
            endTime = LocalTime.MAX;

        List<DMALog> filteringLogList = new ArrayList<>();

        for (DMALog log : logList) {
            LocalTime targetTime = log.getTime();

            if (startTime.compareTo(targetTime) <= 0 && targetTime.compareTo(endTime) <= 0) {
                filteringLogList.add(log);

                if (filteringLogList.size() >= count)
                    break;
            }
        }

        return filteringLogList;
    }

    public static List<LocalTime> getTimeStampFromLogList(List<DMALog> logList, int offset) {
        if (logList == null || logList.size() == 0)
            return new ArrayList<>();

        int position = 0;

        List<LocalTime> timeStamp = new ArrayList<>();

        while (position < logList.size()) {
            timeStamp.add(logList.get(position).getTime());
            position += offset;
        }

        return timeStamp;
    }

    public static LocalTime parseTimeInTracker(String timeString) {
        Pattern minPattern = Pattern.compile("([0-1][0-9]|2[0-4]):([0-5][0-9])");
        Pattern secPattern = Pattern.compile("([0-1][0-9]|2[0-4])(:([0-5][0-9])){2}");
        Pattern milliSecPattern = Pattern.compile("([0-1][0-9]|2[0-4])(:([0-5][0-9])){2}\\.[0-9]{1,3}");

        Matcher minMatcher = minPattern.matcher(timeString);
        Matcher secMatcher = secPattern.matcher(timeString);
        Matcher milliSecMatcher = milliSecPattern.matcher(timeString);

        if (timeString.length() == 5 && minMatcher.find()) {
            int hour, min;

            hour = Integer.parseInt(timeString.substring(0, 2));
            min = Integer.parseInt(timeString.substring(3, 5));
            return LocalTime.of(hour, min);
        } else if (timeString.length() == 8 && secMatcher.find()) {
            int hour, min, sec;

            hour = Integer.parseInt(timeString.substring(0, 2));
            min = Integer.parseInt(timeString.substring(3, 5));
            sec = Integer.parseInt(timeString.substring(6, 8));
            return LocalTime.of(hour, min, sec);
        } else if (timeString.length() >= 10 && timeString.length() <= 12 && milliSecMatcher.find()) {
            int hour, min, sec, nanoSec;

            hour = Integer.parseInt(timeString.substring(0, 2));
            min = Integer.parseInt(timeString.substring(3, 5));
            sec = Integer.parseInt(timeString.substring(6, 8));
            nanoSec = Integer.parseInt(timeString.substring(9)) * nanoSecCorrection[timeString.substring(9).length()];

            return LocalTime.of(hour, min, sec, nanoSec);
        } else {
            System.out.println("Tracker - Time Parsing Error");
            return null;
        }
    }

    public static String getStringFromLongValue(long val) {
        int valLength = 0;
        long valtemp = val;

        while (valtemp > 0) {
            valtemp /= 10;
            valLength++;
        }

        String valString = "";

        for (; valLength < 12; valLength++) {
            valString += "0";
        }

        return valString + val;
    }
}
