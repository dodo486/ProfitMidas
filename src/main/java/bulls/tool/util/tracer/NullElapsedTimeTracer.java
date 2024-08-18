package bulls.tool.util.tracer;

import java.util.concurrent.TimeUnit;

public class NullElapsedTimeTracer extends ElapsedTimeTracer {
    public NullElapsedTimeTracer(long start) {
        super(start);
    }


    public ElapsedTimeTracer copy() {
        return this;
    }

    public void setMark(String mark) {
    }

    public void finalize(String identifier, TimeUnit timeUnit) {
    }


    @Override
    public boolean isNull() {
        return true;
    }


}
