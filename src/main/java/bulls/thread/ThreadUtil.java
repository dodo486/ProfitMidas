package bulls.thread;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThreadUtil {
    public static String getStackTraceString() {
        StringWriter sw = new StringWriter();
        new Throwable("").printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();
        return stackTrace;
    }
}
