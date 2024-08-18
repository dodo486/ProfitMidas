package bulls.thread;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GeneralCoreNamedThreadPoolExecutor extends ThreadPoolExecutor {
    private static final String THREAD_NAME_PATTERN = "%s-%d";
    private MetricRegistry metricRegistry;
    private String metricsPrefix;

    public GeneralCoreNamedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, final TimeUnit unit, final String namePrefix) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<>(), new GeneralCoreThreadFactory(namePrefix));
    }

    private GeneralCoreNamedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, final TimeUnit unit, final String namePrefix, MetricRegistry metricRegistry) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<>(), new GeneralCoreThreadFactory(namePrefix));

        this.metricRegistry = metricRegistry;
        registerQsizeMonitor();
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS);
    }

    public static GeneralCoreNamedThreadPoolExecutor withQSizeMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime, final TimeUnit unit, final String namePrefix) {
        return new GeneralCoreNamedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, namePrefix, new MetricRegistry());
    }

    public void registerQsizeMonitor() {
        metricRegistry.register(MetricRegistry.name(metricsPrefix, "queueSize"), (Gauge<Integer>) () -> getQueue().size());
    }

}
