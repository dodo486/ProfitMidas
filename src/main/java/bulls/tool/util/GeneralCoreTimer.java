package bulls.tool.util;

import bulls.log.DefaultLogger;
import bulls.thread.GeneralCoreThread;

import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class GeneralCoreTimer {

    public GeneralCoreTimer(String threadName) {
        this(threadName, false);
    }

    public GeneralCoreTimer(String threadName, boolean isDaemon) {
        java.util.Timer[] tmp = new java.util.Timer[1];
        tmp[0] = null;
        // java.util.Timer는 new 하는 순간 바로 쓰레드를 생성하고 start()를 호출한다.
        // GeneralCore에서 start()가 호출되도록 하기 위해 GeneralCoreThread를 만들어서 거기에서 Timer 인스턴스를 초기화 하고
        // Semaphore를 통해 Timer가 생성된 뒤 GeneralCoreTimer의 생성자가 종료되도록 실행 순서를 컨트롤한다.
        Semaphore sem = new Semaphore(1);
        try {
            sem.acquire(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GeneralCoreThread initializer = new GeneralCoreThread(() -> {
            tmp[0] = new java.util.Timer(threadName, isDaemon);
            sem.release();
        });
        initializer.start();

        try {
            if (!sem.tryAcquire(1, 10, TimeUnit.SECONDS)) {
                DefaultLogger.logger.error("Exception 발생 10초를 기다렸지만 Timer:{} 가 초기화 되지 않았습니다. 강제로 Timer를 초기화합니다.");
                tmp[0] = new java.util.Timer(threadName, isDaemon);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t = tmp[0];
    }

    private final java.util.Timer t;
    private boolean isRunning = false;

    public TimerTask scheduleAtFixedRate(final Runnable r, long delay, long interval) {
        final TimerTask task = new TimerTask() {
            public void run() {
                r.run();
            }
        };
        isRunning = true;
        t.scheduleAtFixedRate(task, delay, interval);
        return task;
    }

    private int counter;

    public TimerTask scheduleNTimes(final Runnable r, long delay, long interval, long nTimes){
        counter = 0;
        final TimerTask task = new TimerTask() {
            public void run() {
                if(counter >= nTimes) {
                    GeneralCoreTimer.this.cancel();
                    return;
                }
                r.run();
                counter++;
            }
        };
        isRunning = true;
        t.scheduleAtFixedRate(task, delay, interval);
        return task;
    }

    public void schedule(final Runnable r, Date firstTime) {
        final TimerTask task = new TimerTask() {
            public void run() {
                isRunning = true;
                r.run();
                GeneralCoreTimer.this.cancel();
            }
        };
        t.schedule(task, firstTime);
    }

    /**
     * 지연 시간 후 @r을 실행한다
     * @param r
     * @param delay 지연시간(ms)
     */
    public void schedule(final Runnable r, long delay) {
        final TimerTask task = new TimerTask() {
            public void run() {
                isRunning = true;
                r.run();
                GeneralCoreTimer.this.cancel();
            }
        };
        t.schedule(task, delay);
    }

    public synchronized void cancel() {
        if (isRunning) {
            t.cancel();
            isRunning = false;
        }
    }

    public static void main(String[] args) {
        GeneralCoreTimer t = new GeneralCoreTimer("Kill");
        t.schedule(() -> System.out.println("hello"), 2000);

        System.out.println("done");
    }

}
