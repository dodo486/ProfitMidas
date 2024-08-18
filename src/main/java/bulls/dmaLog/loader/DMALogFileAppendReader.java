package bulls.dmaLog.loader;

import bulls.dmaLog.*;
import bulls.thread.GeneralCoreExecutors;
import bulls.tool.util.ObjectPool;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

public enum DMALogFileAppendReader implements DataLoader<DMALogList> {

    Instance;

    private Set<String> pathSet;
    private DMALogFileTypeChecker fileTypeChecker = DMALogFileTypeChecker.getDefault();

    private List<String> filePathList;
    private Map<String, Long> filePointers;
    private SimpleDuplicateLogChecker logChecker;

    private static final String POISON = "POISON";
    private static final int producerThreads = 1;
    private static final int consumerThreads = 2;

    public boolean init() {
        if (pathSet == null || pathSet.size() == 0)
            return false;

        filePathList = new ArrayList<>();
        for (String path : pathSet) {
            File folder = new File(path);
            File[] logFileList = folder.listFiles((dir, name) -> fileTypeChecker.check(name));
            if (logFileList == null)
                continue;

            for (File f : logFileList)
                filePathList.add(f.getAbsolutePath());
        }

        filePointers = new HashMap<>();
        for (String s : filePathList) {
            filePointers.put(s, 0L);
        }

        logChecker = new SimpleDuplicateLogChecker();

        return true;
    }

    public boolean init(Set<String> pathSet, DMALogFileTypeChecker fileTypeChecker) {
        this.pathSet = pathSet;
        this.fileTypeChecker = fileTypeChecker;
        return init();
    }

    public boolean init(Set<String> pathSet) {
        this.pathSet = pathSet;
        return init();
    }

    // https://fire1004.tistory.com/entry/BufferedReader와-RandonAccessFile을-연계한-대용량-파일-처리
    private long calcFileByte(RandomAccessFile reader, String lastData) {
        try {
            String strDiff = new String(lastData.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            long lSize = reader.getFilePointer() - FEPConstantValue.DEFAULT_BUFFER_SIZE * 2;
            boolean isMatch = false;

            if (lSize <= 0) {
                lSize = 0;
            }

            reader.seek(lSize);

            String strLine;
            while ((strLine = reader.readLine()) != null) {
                if (strLine.equals(strDiff)) {
                    isMatch = true;
                    break;
                }
            }

            //if (!isMatch) {
            //    System.out.println("No difference");
            //}

            return reader.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Long.MAX_VALUE;
    }

    @NotNull
    public synchronized DMALogList load() {
        Queue<String> conQueue = new ConcurrentLinkedQueue<>();
        ExecutorService executor = GeneralCoreExecutors.newFixedThreadPool(producerThreads + consumerThreads);
        List<DMALog> logList = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < producerThreads; i++)
            executor.execute(new producerTask(conQueue));

        for (int i = 0; i < consumerThreads; i++)
            executor.execute(new consumerTask(conQueue, logList));

        executor.shutdown();

        while (!executor.isTerminated()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return new DMALogList(logList);
    }

    private class producerTask implements Runnable {
        private final Queue<String> conQueue;

        @Override
        public void run() {
            try {
                for (String path : filePathList) {
                    System.out.println("Read " + path + " " + System.nanoTime());
                    RandomAccessFile seekFile = new RandomAccessFile(path, "r");
                    long nextReadPointer = filePointers.get(path);
                    // 200 bytes 이상 추가됐을 때만 읽기
                    // log 파일에서 heartbeat 한 번이 95 bytes이고, 체결은 보통 200 bytes 이상이다.
                    // 따라서 heartbeat만 들어오는 경우에는 heartbeat를 한 번 무시할 수 있고 (190 bytes)
                    // 체결은 바로 읽을 수 있다.
                    // 또한 업데이트가 없어서 읽을 필요가 없는 파일도 무시하는 효과가 있음
                    if (seekFile.length() < nextReadPointer + 200) {
                        seekFile.close();
                        continue;
                    }

                    seekFile.seek(nextReadPointer);

                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(seekFile.getFD())), FEPConstantValue.DEFAULT_BUFFER_SIZE);

                    String line, lastline = "";

                    while (true) {
                        line = br.readLine();

                        if (line != null) {
                            conQueue.add(line);
                            lastline = line;
                        } else {
                            long position = calcFileByte(seekFile, lastline);

                            if (position != Long.MAX_VALUE)
                                filePointers.put(path, position);

                            seekFile.close();
                            br.close();
                            break;
                        }
                    }
                }

                for (int i = 0; i < consumerThreads; i++)
                    conQueue.add(POISON);

            } catch (IOException | NullPointerException e) {
                for (int i = 0; i < consumerThreads; i++)
                    conQueue.add(POISON);
                e.printStackTrace();
            }
        }

        producerTask(Queue<String> conQueue) {
            this.conQueue = conQueue;
        }
    }

    private class consumerTask implements Runnable {
        private final Queue<String> conQueue;
        private final List<DMALog> logList;

        @Override
        public void run() {
            String value;

            while (true) {
                if ((value = conQueue.poll()) != null) {
                    if (value == POISON)
                        break;

                    DMALog log = DMALogFactory.getLog(value);
                    if (log != null && logChecker.isNotDuplicate(log)) {
                        logList.add(log);
                    }
                }
            }
        }

        consumerTask(Queue<String> conQueue, List<DMALog> logList) {
            this.conQueue = conQueue;
            this.logList = logList;
        }
    }

    static class SimpleDuplicateLogChecker {

        private final ConcurrentHashMap<Integer, Set<Integer>> hashCodeMap;
        private final ObjectPool<Integer> objectPool;

        public SimpleDuplicateLogChecker() {
            hashCodeMap = new ConcurrentHashMap<>();
            objectPool = new ObjectPool<>();
        }

        public Set<Integer> getNewSet() {
            return Collections.newSetFromMap(new ConcurrentHashMap<>());
        }

        public boolean isNotDuplicate(DMALog log) {
            Integer hKey = objectPool.getUniqueObject(log.getPacketBody().hashCode());
            Integer hValue = objectPool.getUniqueObject(log.getCurrentOrderId().hashCode());
            Set<Integer> valueSet;

            if (hashCodeMap.containsKey(hKey)) {
                valueSet = hashCodeMap.get(hKey);

                // packet, orderId의 hashCode가 모두 일치하면 중복으로 판정
                if (valueSet.contains(hValue))
                    return false;

                valueSet.add(hValue);
                return true;
            }

            hashCodeMap.computeIfAbsent(hKey, k -> getNewSet()).add(hValue);
            return true;
        }
    }
}

