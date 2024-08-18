package bulls.log;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public abstract class DefaultLogger {
    static {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.mongodb");
        root.setLevel(Level.OFF);
    }

    public static final Logger logger = LoggerFactory.getLogger(DefaultLogger.class);

}
