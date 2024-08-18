package bulls.tool.util;

import bulls.log.DefaultLogger;
import bulls.tool.performance.PassivePerformanceChecker;
import bulls.tool.performance.VoidFunction;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class BufPool {

    private int poolSize;

    private final int bufferSize;
    private final Queue<ByteBuffer> pool =
            new ConcurrentLinkedQueue<>();

    public BufPool(int poolSize, int bufferSize) {

        if (poolSize < 0) {
            throw new IllegalArgumentException("initialCapacity cannot be "
                    + "smaller than 0.");
        }

        this.poolSize = poolSize;
        this.bufferSize = bufferSize;

        ByteBuffer buf = ByteBuffer.allocateDirect(poolSize * bufferSize);
        divide(buf);
    }

    public void divide(ByteBuffer buf) {

        int pos = 0;
        for (int i = 0; i < poolSize; i++) {
            int max = pos + bufferSize;
            buf.limit(max);
            ByteBuffer slicedBuf = buf.slice();
            pool.add(slicedBuf);
            pos = max;
            buf.position(pos);
        }
    }

    public ByteBuffer acquire() {
        // Poll the oldest buffer ref
        ByteBuffer buf = pool.poll();


        if (buf == null) {
            // Allocate a new byte buffer

            buf = ByteBuffer.allocateDirect(bufferSize);
            pool.add(buf);
            poolSize++;
            DefaultLogger.logger.debug("!!!!!!!!!!!!!!!!!!!! ByteBuffer pool size increased to {}", poolSize);
            return acquire();

        } else {
            // Clear the old buffer
            // buffer.flip();
//			byte[] b = new byte[4];
//			buffer.getFinalizedLimit(b , 2,2);
//			System.out.print("getFinalizedLimit Buffer :" + pool.size());
//			for (int i = 0; i < buffer.limit(); i++)
//				System.out.print(buffer.getFinalizedLimit(i));
//			System.out.println(new String(b));

//			buffer.flip();
//			System.out.println ( buffer.limit() +" " + buffer.position() + " " + buffer.capacity());
//			byte[] b = new byte[buffer.limit()];
//			for (int j = 0; j < buffer.limit(); j++)
//				b[j] = buffer.getFinalizedLimit(j);
//			System.out.println( new String( b));


            buf.clear();
        }

        return buf;
    }

    public void clear() {
        pool.clear();
    }

    public void release(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            throw new NullPointerException("byteBuffer");
        }

        // Queue a new soft reference to the byte buffer

        pool.offer(byteBuffer);
//		System.out.print ("releasing Buffer :" + pool.size() + " ");
//		byte[] b = new byte[byteBuffer.limit()];
//		for (int j = 0; j < byteBuffer.limit(); j++)
//			b[j] = byteBuffer.getFinalizedLimit(j);
//		System.out.println( new String( b));
    }

    public static void main(String[] args) throws UnsupportedEncodingException {


        int size = 5;
        BufPool pool = new BufPool(size, 1024);

        ByteBufferPool pool2 = new ByteBufferPool(size, 1024);

        Runnable r1 = () -> {
            ByteBuffer buf = pool.acquire();
            String str = "[11111111]" + DateFormatter.yyyyMMddHHmmssSSS(new Date(System.currentTimeMillis()));
            buf.put(str.getBytes());
            ByteUtil.printByteBuffer(buf);
            pool.release(buf);
        };


        Runnable r2 = () -> {
            ByteBuffer buf = pool2.acquire();
            String str = "[2222222]" + DateFormatter.yyyyMMddHHmmssSSS(new Date(System.currentTimeMillis()));
            buf.put(str.getBytes());
            ByteUtil.printByteBuffer(buf);
            pool2.release(buf);
        };


        PassivePerformanceChecker checker = new PassivePerformanceChecker(100, 10000, TimeUnit.MICROSECONDS);


        VoidFunction<Integer> normal = () -> {
            r1.run();
            return 1;
        };
        VoidFunction<Integer> soft = () -> {
            r2.run();
            return 1;
        };


        while (!checker.isTestFinished()) {
            checker.delegate("softRef", soft);
            checker.delegate("normal", normal);
        }
    }
}
