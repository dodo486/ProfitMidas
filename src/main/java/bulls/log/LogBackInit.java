package bulls.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import bulls.tool.util.FileUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public enum LogBackInit {
    Instance;

    public void initialize(String logBackXmlFilePath) throws JoranException, IOException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset(); // ignore previous setting whatever it is.
        JoranConfigurator configurator = new JoranConfigurator();

        try (InputStream configStream = FileUtils.openInputStream(logBackXmlFilePath)) {
            configurator.setContext(loggerContext);
            configurator.doConfigure(configStream);
        }

        /// redirect every output to logback...
//        redirect();
    }


    /**
     * Redirects stdout and stderr to logger
     */
    public static void redirect() {
        System.setOut(new PrintStream(System.out) {
            public void print(String s) {
                DefaultLogger.logger.info(s);
            }
        });
        System.setErr(new PrintStream(System.err) {
            public void print(String s) {
                DefaultLogger.logger.error(s);
            }
        });
    }

}
