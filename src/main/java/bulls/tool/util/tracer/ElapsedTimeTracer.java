package bulls.tool.util.tracer;

import bulls.designTemplate.Nullable;
import bulls.log.DefaultLogger;
import bulls.tool.util.GeneralCoreTimer;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class ElapsedTimeTracer implements Nullable {


    private final List<TimeToken> tokenList = new Vector<>();
    private final long initTime;
    private long start;
    private boolean isFinalized = false;
    private long accLocalElapsed = 0;


    public static NullElapsedTimeTracer nullTracer = new NullElapsedTimeTracer(0);

    public static ElapsedTimeTracer createTT(boolean isRemainTT, long start) {
        if (isRemainTT)
            return new ElapsedTimeTracer(start);
        else
            return nullTracer;

    }

    protected ElapsedTimeTracer(long start) {
        this.initTime = start;
        long localElapsed = System.nanoTime() - start;
        accLocalElapsed += localElapsed;
        this.start = start + localElapsed;
    }

    public ElapsedTimeTracer copy() {

        long now = System.nanoTime();
        ElapsedTimeTracer tracer = new ElapsedTimeTracer(initTime);
        tracer.tokenList.addAll(tokenList);
        long localElapsed = System.nanoTime() - now;
        tracer.accLocalElapsed += localElapsed;
        tracer.start = start + localElapsed;
        return tracer;

    }

    // 이전 스레드와 다른 스레드에서 마킹 된다면 오브젝트를 새로 생성한다.
    public void setMark(String mark) {
        long now = System.nanoTime();
        tokenList.add(new TimeToken(mark, start, now));
        long after = System.nanoTime();
        long localElapsed = after - now;

        accLocalElapsed += localElapsed;
        start += localElapsed;
    }

    public void finalize(String identifier, TimeUnit timeUnit) {
        if (isFinalized) {
            setMark(identifier);
            return;
        }
        isFinalized = true;
        setMark(identifier);
        GeneralCoreTimer timer = new GeneralCoreTimer("ES_" + identifier);
        timer.schedule(() -> {
            synchronized (this.tokenList) {
                Iterator<TimeToken> it = tokenList.iterator();
                DefaultLogger.logger.info(" =================== Trigger Time: {} , accTracerLocalElapsed: {}", initTime, accLocalElapsed);
                while (it.hasNext()) {
                    TimeToken t = it.next();
                    t.printElapsedTime(timeUnit);
                    it.remove();
                }
            }
        }, 1000);

    }

    @Override
    public boolean isNull() {
        return false;
    }

    static class TimeToken {
        String mark;
        long init;
        long now;

        TimeToken(String mark, long init, long now) {
            this.mark = Thread.currentThread().getName() + " " + mark;
            this.init = init;
            this.now = now;
        }

        public void printElapsedTime(TimeUnit unit) {
            DefaultLogger.logger.info("{}: {}", mark, (unit.convert(now - init, TimeUnit.NANOSECONDS)));
        }
    }
}
