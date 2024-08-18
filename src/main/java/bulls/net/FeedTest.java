package bulls.net;

import ch.qos.logback.core.joran.spi.JoranException;
import bulls.designTemplate.observer.PriorityObStation;
import bulls.exception.ConfigurationException;
import bulls.exception.InvalidFeedConfigException;
import bulls.feed.abstraction.Feed;
import bulls.feed.dc.PrimitiveDC;
import bulls.feed.launch.FeedLaunchCenter;
import bulls.log.OnceAPeriodLogger;
import bulls.staticData.TempConf;
import bulls.tool.conf.KrxConfiguration;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

public class FeedTest {
    public static void main(String[] args) throws ConfigurationException, JoranException, IOException {
        KrxConfiguration conf = new KrxConfiguration(args[0]);
        TempConf.init(conf);

        PriorityObStation<Feed> obStation = new PriorityObStation<>("FeedObStation");
        try {
            FeedLaunchCenter.Instance.init(conf, obStation);
            FeedLaunchCenter.Instance.start();
        } catch (InvalidFeedConfigException e) {
            e.printStackTrace();
            OnceAPeriodLogger.Instance.tryPrintErr(1, ChronoUnit.SECONDS, "Feed설정이 잘못되었습니다. 프로그램을 종료합니다", true);
            System.exit(1);
        }

        PrimitiveDC.Instance.setFeedObserver(obStation, FeedLaunchCenter.Instance.getMonitoringFeedTrCodeSet());
        PrimitiveDC.Instance.monitorBidAskOf("KR7005930003", "BidAskTest", ((obName, codeUpdatedFromMarket, newData) -> System.out.println(newData)));
        PrimitiveDC.Instance.monitorPriceOf("KR7005930003", "PriceInfoTest", ((obName, codeUpdatedFromMarket, newData) -> System.out.println(newData)));
    }
}
