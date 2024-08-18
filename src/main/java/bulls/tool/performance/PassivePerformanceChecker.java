package bulls.tool.performance;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PassivePerformanceChecker {

    private final ConcurrentHashMap<String, ElapsedTimeInfo> infoMap;
    private final int times;
    private final int skipTimes; // skip first 'skipTimes' appearance for more accurate measuring.
    private final TimeUnit timeUnit;
    private boolean isTestFinished = false;


    public PassivePerformanceChecker(int skipTimes, int times, TimeUnit timeUnit) {
        if (skipTimes >= times)
            throw new IllegalArgumentException(" SkipTimes can not be bigger thant times");

        this.times = times;
        this.timeUnit = timeUnit;
        this.skipTimes = skipTimes;
        infoMap = new ConcurrentHashMap<>();
    }

    public synchronized <R> R delegate(String procedureName, VoidFunction<R> job) {
        ElapsedTimeInfo info = infoMap.getOrDefault(procedureName, new ElapsedTimeInfo(procedureName));

        if (info.isFinished)
            return job.doSome();

        if (info.skipCount < skipTimes) {
            info.skipCount++;
            String sb = procedureName +
                    " skipping " +
                    info.skipCount;
            System.out.println(sb);
            infoMap.put(procedureName, info);
            return job.doSome();
        }

        long start = System.nanoTime();
        R r = job.doSome();
        long end = System.nanoTime();
        Long elapsedTime = timeUnit.convert((end - start), TimeUnit.NANOSECONDS);

        info.addNewSample(elapsedTime);

        infoMap.put(procedureName, info);

        return r;


    }


    public synchronized void delegate(String procedureName, Runnable job) {
        ElapsedTimeInfo info = infoMap.getOrDefault(procedureName, new ElapsedTimeInfo(procedureName));

        if (info.isFinished) {
            job.run();
            return;
        }

        if (info.skipCount < skipTimes) {
            info.skipCount++;
            String sb = procedureName +
                    " skipping " +
                    info.skipCount;
            System.out.println(sb);
            infoMap.put(procedureName, info);
            job.run();
            return;
        }

        long start = System.nanoTime();
        job.run();
        long end = System.nanoTime();
        Long elapsedTime = timeUnit.convert((end - start), TimeUnit.NANOSECONDS);

        info.addNewSample(elapsedTime);

        infoMap.put(procedureName, info);

    }

    public boolean isTestFinished() {
        return isTestFinished;
    }

    private final class ElapsedTimeInfo {

        private int hitCount = 0;
        private int skipCount = 0;
        private boolean isFinished = false;
        private final String procedureName;

        private final ArrayList<Long> elapsedTimeList;

        public ElapsedTimeInfo(String procedureName) {
            this.procedureName = procedureName;
            elapsedTimeList = new ArrayList<>(times);
        }

        public void addNewSample(long elapsedTime) {
            hitCount++;
            elapsedTimeList.add(elapsedTime);
            if (hitCount >= times) {
                isFinished = true;
                isTestFinished = true;
                printResult();
            }
        }

        public void printResult() {

            SummaryStatistics summaryFull = elapsedTimeList.stream()
                    .mapToLong(l -> l).collect(SummaryStatistics::new, SummaryStatistics::addValue, SummaryStatistics::copy);

            int length = elapsedTimeList.size();
            SummaryStatistics summaryMid = elapsedTimeList.stream().sorted()
                    .skip(length / 100) // ignore fastest 10%
                    .limit((long) (length * 0.98)) // ignore latest 10%
                    .mapToLong(l -> l).collect(SummaryStatistics::new, SummaryStatistics::addValue, SummaryStatistics::copy);

            System.out.println();
            System.out.println("================= Procedure Name : " + procedureName);
            System.out.println("=================== Full summary : " + summaryFull.toString());
            System.out.println("===== 80% Median closest summary :" + summaryMid.toString());
            System.out.println();
        }

    }


}
