package bulls.net;

import ch.qos.logback.core.joran.spi.JoranException;
import bulls.dmaLog.DMALogFileTypeChecker;
import bulls.dmaLog.loader.DMALogDataCenter;
import bulls.dmaLog.marketshare.MarketShareAfternoonDBUploader;
import bulls.dmaLog.marketshare.MarketShareCenter;
import bulls.dmaLog.loader.DMALogFileAppendReader;
import bulls.dmaLog.transactiontracker.FEPLogTSDBLiveUploader;
import bulls.dmaLog.transactiontracker.TransactionTracker;
import bulls.exception.ConfigurationException;
import bulls.staticData.AliasManager;
import bulls.staticData.TempConf;
import bulls.tool.conf.KrxConfiguration;
import bulls.websocket.jetty.FEPAnalyzerServlet;
import bulls.websocket.jetty.WSServer;

import java.io.IOException;
import java.util.Set;

public class FEPAnalyzer {
    public static void main(String[] args) throws ConfigurationException, IOException, JoranException {
        if (args.length > 0) {
            KrxConfiguration conf = new KrxConfiguration(args[0]);
            TempConf.init(conf);
        }

        int wsPort = TempConf.FEP_ANALYZER_WS_PORT;
        String wsIP = TempConf.FEP_ANALYZER_WS_BIND_IP;
        String equityPath = TempConf.FEP_ANALYZER_LOG_PATH + "LS";
        String derivPath = TempConf.FEP_ANALYZER_LOG_PATH + "LF";

        Set<String> pathSet = Set.of(equityPath, derivPath);
        DMALogFileTypeChecker fileTypeChecker = DMALogFileTypeChecker.getDefault();

        boolean isLive = true;

        if (args.length == 2 && args[1].equals("TEST")) {
            System.out.println("Test Mode ON");
            isLive = false;
            wsIP = "172.28.203.107";
            wsPort = 4623;
            pathSet = Set.of("samplelog/equity", "samplelog");
        }

        WSServer fepAnalyzerWSServer = new WSServer(wsIP, wsPort);
        fepAnalyzerWSServer.setServer(FEPAnalyzerServlet.class, "ws-oraclearena", "FEP");
        fepAnalyzerWSServer.start();

        AliasManager.Instance.touch();

        TransactionTracker.Instance.init();
        FEPLogTSDBLiveUploader.Instance.start();
        DMALogFileAppendReader.Instance.init(pathSet, fileTypeChecker);

        DMALogDataCenter manager = DMALogDataCenter.Instance;
        manager.setLoader(DMALogFileAppendReader.Instance);
        manager.addObserver("TransactionTracker", TransactionTracker.Instance);
        manager.addObserver("MarketShareCenter", MarketShareCenter.Instance);
        if (isLive)
            manager.addObserver("FEPLogTSDBLiveUploader", FEPLogTSDBLiveUploader.Instance);

        // TempConf.FEP_ANALYZER_POLLING_SEC = 0이면 polling mode 해제
        manager.start(TempConf.FEP_ANALYZER_POLLING_SEC);
        if (isLive)
            MarketShareAfternoonDBUploader.Instance.threadRun();
    }
}
