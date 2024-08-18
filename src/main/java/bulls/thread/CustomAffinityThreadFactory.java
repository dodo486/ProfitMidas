package bulls.thread;

import bulls.log.DefaultLogger;
import bulls.log.OnceAPeriodLogger;
import bulls.server.ServerMessageSender;
import net.openhft.affinity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

import static net.openhft.affinity.AffinityStrategies.*;

/**
 * 쓰레드가 지정된 Core에서 실행되도록 해주는 ThreadFactory.
 * net.openhft.affinity.AffinityThreadFactory에 버그가 있어서 이걸 기반으로 일부 수정해서 새로 만들었음.
 */
public class CustomAffinityThreadFactory implements ThreadFactory {
    private final String name;
    private final boolean daemon;
    @NotNull
    private final AffinityStrategy[] strategies;
    @Nullable
    private AffinityLock lastAffinityLock;
    private int id;

    private static final ConcurrentHashMap<Integer, String> affinityThreadNameMap = new ConcurrentHashMap<>();

    public CustomAffinityThreadFactory(String name, AffinityStrategy... strategies) {
        this(name, true, strategies);
    }

    public CustomAffinityThreadFactory(String name, boolean daemon, @NotNull AffinityStrategy... strategies) {
        this.lastAffinityLock = null;
        this.id = 1;
        this.name = name;
        this.daemon = daemon;
        this.strategies = strategies.length == 0 ? new AffinityStrategy[]{AffinityStrategies.ANY} : strategies;
    }

    @NotNull
    public synchronized Thread newThread(@NotNull final Runnable r) {
        String name2 = this.id <= 1 ? this.name : this.name + '-' + this.id;
        ++this.id;
        Thread t = new Thread(new Runnable() {
            public void run() {
                AffinityLock al = CustomAffinityThreadFactory.this.acquireLockBasedOnLast();
                DefaultLogger.logger.info("AffinityLock : {} (CPU ID : {})", al, al.cpuId());
                if (!al.isAllocated()) {
                    OnceAPeriodLogger.Instance.tryPrintErr(1, ChronoUnit.SECONDS, name2 + " assign CPU failed!", true);
                    ServerMessageSender.writeServerMessage(CustomAffinityThreadFactory.class, "Affinity", "경고", "Affinity 설정에 실패한 Thread가 있습니다.");
                }
                else {
                    // 같은 코어에 할당된 thread가 있는지 확인
                    if (affinityThreadNameMap.containsKey(al.cpuId())) {
                        DefaultLogger.logger.error("Affinity Warning : CPU ID {} 에 중복하여 Thread가 할당되었습니다. 할당됨={}, 추가={}", al.cpuId(), affinityThreadNameMap.get(al.cpuId()), al);
                        ServerMessageSender.writeServerMessage(CustomAffinityThreadFactory.class, "Affinity", "경고",
                                "Affinity Warning : CPU ID {} 에 중복하여 Thread가 할당되었습니다. 할당됨={}, 추가={}", al.cpuId(), affinityThreadNameMap.get(al.cpuId()), al.toString());
                    } else {
                        affinityThreadNameMap.put(al.cpuId(), al.toString());
                    }
                }
                Throwable var2 = null;

                try {
                    r.run();
                } catch (Throwable var11) {
                    var2 = var11;
                    throw var11;
                } finally {
                    if (al != null) {
                        if (var2 != null) {
                            try {
                                al.close();
                            } catch (Throwable var10) {
                                var2.addSuppressed(var10);
                            }
                        } else {
                            al.close();
                        }
                    }
                }
            }
        }, name2);
        t.setDaemon(this.daemon);
        DefaultLogger.logger.info("CustomAffinityThread [{}] is dedicated on isolated CPU (check /tmp for details)! current stack:\n{}", name2,
                ThreadUtil.getStackTraceString());
        return t;
    }

    private synchronized AffinityLock acquireLockBasedOnLast() {
        AffinityLock al = AffinityLock.acquireLock();
        if (al.cpuId() >= 0) {
            this.lastAffinityLock = al;
        }
        return al;
    }

    public static void main(String[] args) {
        ExecutorService ES = Executors.newFixedThreadPool(4,
                new AffinityThreadFactory("bg", SAME_CORE, DIFFERENT_SOCKET, ANY));
        for (int i = 0; i < 35; i++)
            ES.submit(new Callable<Void>() {
                @Override
                public Void call() throws InterruptedException {
                    DefaultLogger.logger.info("CPUID : {}", Affinity.getCpu());
                    Thread.sleep(3000);
                    DefaultLogger.logger.info("CPUID : {}", Affinity.getCpu());
                    return null;
                }
            });
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\nThe assignment of CPUs is\n" + AffinityLock.dumpLocks());
        ES.shutdown();
        try {
            ES.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //common.oaSub.staticData.ELW.ELWIdentifier

    }
}
