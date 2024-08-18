package bulls.dmaLog.loader;

import bulls.analysis.AnalysisUtil;
import bulls.designTemplate.GeneralFileReader;
import bulls.designTemplate.ObjectGenerator;
import bulls.dmaLog.*;
import bulls.log.DefaultLogger;
import bulls.server.enums.SelectServerLocation;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class FEPDirectoryLoader implements ObjectGenerator<FEPDailyContractLogData>, Iterable<FEPDailyContractLogData>, Iterator<FEPDailyContractLogData> {
    private final String filterString;
    private final SelectServerLocation location;
    private final DMALogFileTypeChecker fileTypeChecker;

    private BlockingQueue<File> fileQueue;
    private String directory;
    private int totalFileCount;

    private FEPDailyContractLogData lastUnusedData = null;

    public FEPDirectoryLoader(String directory) {
        this(directory, null, SelectServerLocation.SEOUL, DMALogFileTypeChecker.getDefault());
    }

    public FEPDirectoryLoader(String directory, SelectServerLocation location) {
        this(directory, null, location, DMALogFileTypeChecker.getDefault());
    }

    public FEPDirectoryLoader(String directory, String filter, SelectServerLocation location) {
        this(directory, filter, location, DMALogFileTypeChecker.getDefault());
    }

    public FEPDirectoryLoader(String directory, String filter, SelectServerLocation location, DMALogFileTypeChecker fileTypeChecker) {
        this.directory = directory;
        this.filterString = filter;
        this.location = location;
        this.fileTypeChecker = fileTypeChecker;
    }

    @Override
    public boolean hasNext() {
        if (lastUnusedData != null)
            return true;

        lastUnusedData = _next();
        return lastUnusedData != null;
    }

    @NotNull
    public List<FEPDailyContractLogData> load() {
        if (fileQueue == null) {
            System.out.println("FEPDirectoryLoader Error : initialize error");
            return new ArrayList<>();
        }

        List<FEPDailyContractLogData> logDataList = new ArrayList<>();

        while (true) {
            FEPDailyContractLogData contractLogData = next();
            if (contractLogData == null)
                break;

            logDataList.add(contractLogData);
        }

        close();

        return logDataList;
    }

    @Override
    public FEPDailyContractLogData next() {
        if (lastUnusedData != null) {
            var data = lastUnusedData;
            lastUnusedData = null;
            return data;
        }

        return _next();
    }

    private FEPDailyContractLogData _next() {
        File f = fileQueue.poll();

        if (f == null)
            return null;

        int idx = totalFileCount - fileQueue.size();
        System.out.println("Read " + f.getAbsolutePath() + " (" + idx + "/" + totalFileCount + String.format(", %.1f%%)", (double) idx * 100 / totalFileCount));

        GeneralFileReader reader;
        LocalDate date;

        if (f.isFile() && f.getName().endsWith(".zip")) {
            reader = new DMAZipReader(f.getAbsolutePath(), fileTypeChecker);
            try {
                date = AnalysisUtil.getLocalDateFromDateString(f.getName().split("_")[2].substring(0, 8));
            } catch (Exception e) {
                return next();
            }
        } else if (f.isDirectory()) {
            reader = new DMALogReader(f.getAbsolutePath(), fileTypeChecker);
            try {
                date = AnalysisUtil.getLocalDateFromDateString(f.getName().split("_")[0]);
            } catch (Exception e) {
                return next();
            }
        } else
            return next();

        if (!reader.init()) {
            System.out.println("FEPDirectoryLoader : Failed to initialize Reader (" + f.getAbsolutePath() + ")");
            return next();
        }

        Set<String> packetSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Map<String, List<DMALog>> fullLogMap = new HashMap<>();
        Map<String, List<TradeDMALog>> tradeLogMap = new HashMap<>();
        Map<String, List<ReportDMALog>> reportLogMap = new HashMap<>();
        Map<String, List<RequestDMALog>> requestLogMap = new HashMap<>();

        int count = 0;

        while (true) {
            String rawPacket = reader.next();
            if (rawPacket == null)
                break;

            DMALog log = DMALogFactory.getLog(rawPacket);

            if (log == null || packetSet.contains(log.getPacketBody()))
                continue;

            fullLogMap.computeIfAbsent(log.getIsinCode(), k -> new ArrayList<>()).add(log);

            if (log instanceof TradeDMALog)
                tradeLogMap.computeIfAbsent(log.getIsinCode(), k -> new ArrayList<>()).add((TradeDMALog) log);
            else if (log instanceof ReportDMALog)
                reportLogMap.computeIfAbsent(log.getIsinCode(), k -> new ArrayList<>()).add((ReportDMALog) log);
            else if (log instanceof RequestDMALog)
                requestLogMap.computeIfAbsent(log.getIsinCode(), k -> new ArrayList<>()).add((RequestDMALog) log);
            else
                continue;

            packetSet.add(log.getPacketBody());

            count++;
            if (count % 250_000 == 1)
                DefaultLogger.logger.info("FEP Packet Parsing (" + f.getAbsolutePath() + ") " + count + " | " + log.getFileName() + " " + log.getTime());
        }

        return new FEPDailyContractLogData(date, f.getName(), fullLogMap, tradeLogMap, reportLogMap, requestLogMap);
    }

    @Override
    public boolean init() {
        File dir = new File(directory);

        if (!dir.exists()) {
            System.out.println("FEPDirectoryLoader Error : " + directory + " is not exist");
            return false;
        }

        if (!dir.isDirectory()) {
            System.out.println("FEPDirectoryLoader Error : " + directory + " is not a directory");
            return false;
        }

        List<File> fileList;
        if (filterString == null)
            fileList = Arrays.stream(Objects.requireNonNull(dir.listFiles())).collect(Collectors.toList());
        else
            fileList = Arrays.stream(Objects.requireNonNull(dir.listFiles())).filter(x -> x.getName().contains(filterString)).collect(Collectors.toList());

        if (fileList.size() == 0) {
            System.out.println("FEPDirectoryLoader Error : " + directory + " is empty");
            return false;
        }

        fileQueue = new LinkedBlockingQueue<>();
        if (location.containsSeoul())
            fileQueue.addAll(fileList.stream().filter(x -> x.getName().contains("seoul")).sorted().collect(Collectors.toList()));
        if (location.containsPusan())
            fileQueue.addAll(fileList.stream().filter(x -> x.getName().contains("pusan")).sorted().collect(Collectors.toList()));

        totalFileCount = fileQueue.size();
        return true;
    }

    @Override
    public boolean close() {
        fileQueue = null;
        directory = null;
        return true;
    }

    @NotNull
    @Override
    public Iterator<FEPDailyContractLogData> iterator() {
        return this;
    }
}
