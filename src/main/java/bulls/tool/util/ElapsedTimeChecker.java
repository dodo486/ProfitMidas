package bulls.tool.util;

import bulls.log.DefaultLogger;

import java.util.concurrent.TimeUnit;

public class ElapsedTimeChecker {

    private static long start;
    private static long end;

    public static void setStart() {
        start = System.nanoTime();
    }

    public static void setEnd(TimeUnit timeUnit) {
        end = System.nanoTime();
        printElapsedTime(timeUnit);
    }

    public static Long getEnd(TimeUnit timeUnit) {
        end = System.nanoTime();
        return timeUnit.convert(end - start, TimeUnit.NANOSECONDS);
    }

    private static void printElapsedTime(TimeUnit unit) {
        DefaultLogger.logger.info("Elapsed Time : {}", (unit.convert(end - start, TimeUnit.NANOSECONDS)));
    }
}
