package bulls.feed.dc;

import bulls.datastructure.Pair;
import bulls.log.DefaultLogger;
import bulls.log.OnceAPeriodLogger;
import bulls.server.ServerMessageSender;
import bulls.staticData.TempConf;
import bulls.thread.CustomAffinityThreadFactory;
import bulls.thread.GeneralCoreExecutors;
import bulls.thread.GeneralCoreThread;
import bulls.tool.util.MultiScheduleTimer;
import bulls.tool.util.PeriodicRunnable;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import net.openhft.affinity.AffinityStrategies;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class CodeHashObStation<T> {
    static class CodeHashObStationLogger {
        final int workerSize;
        final String name;
        final long baseStartNanoTime;
        final LocalDateTime baseStartLocalDateTime;
        ConcurrentLinkedQueue<Pair<Long, Long>>[] logDataArr;

        public CodeHashObStationLogger(int workerSize, String name) {
            this.workerSize = workerSize;
            this.name = name;
            logDataArr = new ConcurrentLinkedQueue[workerSize];
            for (int i = 0; i < workerSize; ++i) {
                logDataArr[i] = new ConcurrentLinkedQueue<>();
            }
            baseStartNanoTime = System.nanoTime();
            baseStartLocalDateTime = LocalDateTime.now();
            try {
                Files.writeString(Path.of("CodeHashObStationLog_" + name + ".log"), name + "," + baseStartNanoTime + "," + baseStartLocalDateTime + "\n", StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            MultiScheduleTimer.Instance.registerPeriodic(new PeriodicRunnable("CodeHashObStationWriter_" + name,
                    this::writeLog, 5, 0));
        }

        public void addLog(int workerIdx, long startTime, long endTime) {
            logDataArr[workerIdx].add(new Pair<>(startTime, endTime));
        }

        public void writeLog() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < workerSize; ++i) {
                Pair<Long, Long> data;
                while ((data = logDataArr[i].poll()) != null) {
                    sb.append(i).append(",").append(data.firstElem - baseStartNanoTime).append(",").append(data.secondElem - baseStartNanoTime).append("\n");
                }
            }
            try {
                Files.writeString(Path.of("CodeHashObStationLog_" + name + ".log"), sb.toString(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private CodeHashDataProducer<T> producer;

    private final boolean useDisruptor;
    private final String purpose;

    ExecutorService exec = GeneralCoreExecutors.newFixedThreadPool(10);

    CodeHashObStationLogger logger;

    public CodeHashObStation() {
        obMapWithCode = new HashMap<>();
        obMapArr = new HashMap<>();
        obKeyToCodeMap = new HashMap<>();
//        obKeySet = new HashSet<>();
        useDisruptor = false;
        purpose = "SimpleCodeHashObStation";
    }

    public CodeHashObStation(String purpose, int workerSize, ProducerType producerType, WaitStrategy waitStrategy, Collection<EmergencyHandler> emergencyHandlerCollection) {
        obMapWithCode = new HashMap<>();
        obMapArr = new HashMap<>();
        obKeyToCodeMap = new HashMap<>();
//        obKeySet = new HashSet<>();
        this.purpose = purpose;
        if (workerSize == 0) {
            useDisruptor = false;
            return;
        }

        useDisruptor = true;
        int bufferSize = 8192 * 8;
//        Disruptor<ConcreteCodeHashData> disruptor = new Disruptor<>( ConcreteCodeHashData::new, bufferSize, Executors.newCachedThreadPool());
//        disruptor = new Disruptor<>(ConcreteFeed::new, bufferSize, Executors.newCachedThreadPool(new NamedThreadFactory("RawFeedHandler")));


        Disruptor<ConcreteCodeHashData<T>> disruptor;
        if (TempConf.DISRUPTOR_BLOCKING) {
            DefaultLogger.logger.info("DISRUPTOR_BLOCKING is true. Blocking mode is forced to Disruptor for {}:{}", this.getClass().getName(), purpose);
            waitStrategy = new BlockingWaitStrategy();
        }
        if (waitStrategy instanceof BlockingWaitStrategy) {
            ThreadFactory factory = r -> new GeneralCoreThread(purpose, r);
            disruptor = new Disruptor<>(ConcreteCodeHashData::new, bufferSize, factory, producerType, waitStrategy);
        } else {
            ThreadFactory factory = new CustomAffinityThreadFactory(purpose, AffinityStrategies.ANY);
            disruptor = new Disruptor<>(ConcreteCodeHashData::new, bufferSize, factory, producerType, waitStrategy);
        }
//        Disruptor<ConcreteCodeHashData> disruptor = new Disruptor<>( ConcreteCodeHashData::new, bufferSize, new NamedThreadFactory(purpose), producerType, waitStrategy);
        // Connect the handler

        WorkHandler[] workHandlers = new WorkHandler[workerSize];
        if (TempConf.ENABLE_CODEHASHOBSTATION_LOGGING) {
            logger = new CodeHashObStationLogger(workerSize, this.purpose);
            for (int i = 0; i < workerSize; ++i) {
                final int idx = i;
                workHandlers[idx] = (WorkHandler<ConcreteCodeHashData<T>>) concreteCodeHashData -> {
                    long startTime = 0, endTime;
                    try {
                        startTime = System.nanoTime();
                        concreteCodeHashData.observer.update(concreteCodeHashData.obName, concreteCodeHashData.code, concreteCodeHashData.newData);
                    } catch (Throwable e) {
                        DefaultLogger.logger.error(e.toString());
                        for (var emergency : emergencyHandlerCollection)
                            emergency.emergencyShutdown();
                        ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "피드처리오류", "[CodeHashObStation]", e);
                        throw e;
                    } finally {
                        endTime = System.nanoTime();
                        logger.addLog(idx, startTime, endTime);
                    }
                };
            }
        } else {
            Arrays.fill(workHandlers, (WorkHandler<ConcreteCodeHashData<T>>) concreteCodeHashData -> {
                try {
                    concreteCodeHashData.observer.update(concreteCodeHashData.obName, concreteCodeHashData.code, concreteCodeHashData.newData);
                } catch (Throwable e) {
                    DefaultLogger.logger.error(e.toString());
                    for (var emergency : emergencyHandlerCollection)
                        emergency.emergencyShutdown();
                    ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "피드처리오류", "[CodeHashObStation]", e);
                    throw e;
                }
            });
        }

        disruptor.handleEventsWithWorkerPool(workHandlers);
        disruptor.start();
        producer = new CodeHashDataProducer<>(disruptor.getRingBuffer());
    }

    private final HashMap<String, HashSet<CodeObserverObj<T>>> obMapWithCode; // isinCode, observerKey, observer
    private final HashMap<String, CodeObserverObj<T>[]> obMapArr; // isinCode, observerKey, observer
    private final HashMap<String, Integer> observerCountMap = new HashMap<>(); // obMapWithCode 의 각 Code 당 등록된 CodeObserver 등록된 갯수
    private final HashMap<String, String> obKeyToCodeMap;


    public void addMultiCodeObserver(Set<String> codeSet, CodeObserver<T> ob, String obName) {
        synchronized (this) {
            for (String code : codeSet) {
                CodeObserverObj<T> newCodeObserverObj = new CodeObserverObj<>(obName, ob);
                HashSet<CodeObserverObj<T>> set = obMapWithCode.getOrDefault(code, new HashSet<>());
                set.add(newCodeObserverObj);
                int observerCount = set.size();
                CodeObserverObj<T>[] arr = (CodeObserverObj<T>[]) Array.newInstance(CodeObserverObj.class, set.size());
                int i = 0;
                for (CodeObserverObj<T> codeObserverObj : set) {
                    arr[i++] = codeObserverObj;
                }
                obMapArr.put(code, arr);
                observerCountMap.put(code, observerCount);
                obMapWithCode.put(code, set);
                obKeyToCodeMap.put(obName, code);
            }
        }
    }

    public void addObserver(String code, CodeObserver<T> ob, String obName) {
        synchronized (this) {
            CodeObserverObj<T> newCodeObserverObj = new CodeObserverObj<>(obName, ob);

            String prevCodeOfSameObName = obKeyToCodeMap.get(obName);
            // 등록된 옵저버이름이 기존에 있고 그를 업데이트 하는 code 가 기존과 다를때
            if (prevCodeOfSameObName != null && !prevCodeOfSameObName.equals(code)) {
                HashSet<CodeObserverObj<T>> oldSet = obMapWithCode.get(prevCodeOfSameObName);
                oldSet.remove(newCodeObserverObj);
                CodeObserverObj<T>[] arr = (CodeObserverObj<T>[]) Array.newInstance(CodeObserverObj.class, oldSet.size());
                DefaultLogger.logger.info("같은 observerName {} 등록으로 {} 를 듣던 observer 가 이제부터 {} 를 모니터링합니다.", obName, prevCodeOfSameObName, code);
                int i = 0;
                for (CodeObserverObj<T> codeObserverObj : oldSet) {
                    arr[i++] = codeObserverObj;
                }
                obMapArr.put(prevCodeOfSameObName, arr);
                obMapWithCode.put(prevCodeOfSameObName, oldSet);
                observerCountMap.put(prevCodeOfSameObName, oldSet.size());
                obKeyToCodeMap.remove(obName);
            }

            HashSet<CodeObserverObj<T>> set = obMapWithCode.getOrDefault(code, new HashSet<>());
            set.add(newCodeObserverObj);
            int observerCount = set.size();
            CodeObserverObj<T>[] arr = (CodeObserverObj<T>[]) Array.newInstance(CodeObserverObj.class, set.size());
            int i = 0;
            for (CodeObserverObj<T> codeObserverObj : set) {
                arr[i++] = codeObserverObj;
            }
            obMapArr.put(code, arr);
            observerCountMap.put(code, observerCount);
            obMapWithCode.put(code, set);
            obKeyToCodeMap.put(obName, code);
        }
    }

    public void removeObserver(String code, String observerKey) {
        if (observerKey == null)
            return;

        synchronized (this) {
            obKeyToCodeMap.remove(observerKey);
            HashSet<CodeObserverObj<T>> set = obMapWithCode.getOrDefault(code, new HashSet<>());
            Optional<CodeObserverObj<T>> opObj = set.stream().filter(obj -> obj.observerName.equals(observerKey)).findFirst();
            if (opObj.isEmpty())
                return;

            CodeObserverObj<T> obj = opObj.get();
            set.remove(obj);
            obMapWithCode.put(code, set);
            CodeObserverObj<T>[] arr = (CodeObserverObj<T>[]) Array.newInstance(CodeObserverObj.class, set.size());
            int i = 0;
            for (CodeObserverObj<T> codeObserverObj : set) {
                arr[i++] = codeObserverObj;
            }
            obMapArr.put(code, arr);
            int observerCount = set.size();
            observerCountMap.put(code, observerCount);

//            // 같은 코드를 모니터링 하는 다른 옵저버가 있을수도 있다. 그래서 지울때는 한번더 체크
//            Optional<Map.Entry<String, HashSet<CodeObserverObj>>> entry = obMapWithCode.entrySet().stream()
//                    .filter( en-> en.getValue().contains(obj))
//                    .findAny();
//
//            if(!entry.isPresent())
//                obKeyToCodeMap.remove(observerKey);

        }
    }

//    public void setObserverComparator(String code, Comparator<Map.Entry<String, CodeObserver<T>>> observerComparator) {
//        synchronized (this){
//            ConcurrentSkipListMap<String , CodeObserver<T>> newSortedMap = new ConcurrentSkipListMap(observerComparator);
//            ConcurrentSkipListMap<String, CodeObserver<T>> map = obMapWithCode.getOrDefault(code, new ConcurrentSkipListMap());
//            newSortedMap.putAll(map);
//            obMapWithCode.put(code, newSortedMap);
//        }
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CodeHashObserver Station status\n");

        for (Map.Entry<String, HashSet<CodeObserverObj<T>>> en : obMapWithCode.entrySet()) {
            sb.append("Monitoring Code:");
            sb.append(en.getKey());
            sb.append("\n");
            for (CodeObserverObj<T> obj : en.getValue()) {
                sb.append("                             observerKey:");
                sb.append(obj.observerName);
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    public void notify(String code, T newData) {
        if (!useDisruptor) {
            handleNotify(code, newData);
            return;
        }

        CodeObserverObj<T>[] arr = obMapArr.get(code);
        if (arr == null)
            return;

        for (CodeObserverObj<T> obj : arr) {
            try {
                producer.onData(obj.observerName, obj.observer, code, newData);
            } catch (InsufficientCapacityException e) {
                String msg = String.format("Ringbuffer is full. Increase the buffer size. Temporarily process data on another thread. %s %s", obj.observerName, code);
                OnceAPeriodLogger.Instance.tryPrintErr(10, ChronoUnit.MINUTES, msg, "RingBuffer Full", true);
                exec.execute(() -> producer.onDataBlocking(obj.observerName, obj.observer, code, newData));
            }
        }
    }

    public void handleNotify(String code, T newData) {
        CodeObserverObj<T>[] arr = obMapArr.get(code);

        if (arr == null)
            return;

        for (CodeObserverObj<T> obj : arr) {
            obj.observer.update(obj.observerName, code, newData);
        }
    }

    static class CodeObserverObj<T> {
        private final String observerName;
        private final CodeObserver<T> observer;

        public CodeObserverObj(String observerName, CodeObserver<T> observer) {
            this.observer = observer;
            this.observerName = observerName;
        }

        @Override
        public int hashCode() {
            return observerName.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CodeObserverObj<?> that = (CodeObserverObj<?>) o;
            return observerName.equals(that.observerName);
        }
    }
}
