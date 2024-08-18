package bulls.tool.util;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ByteBufferPool {
    private int poolSize;

    private final int bufferSize;
    // Used to store soft references to byte buffers
    private final Queue<SoftReference<ByteBuffer>> pool =
            new ConcurrentLinkedQueue<SoftReference<ByteBuffer>>();

    public ByteBufferPool(int poolSize, int bufferSize) {
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
            SoftReference<ByteBuffer> ref = new SoftReference<>(buf.slice());
            pool.add(ref);
            pos = max;
            buf.position(pos);
        }
    }

    public ByteBuffer acquire() {
        // Poll the oldest buffer ref
        SoftReference<ByteBuffer> byteBufferRef = pool.poll();

        // Tmp
        ByteBuffer buffer;

        if (byteBufferRef == null) {
            // Allocate a new byte buffer

            buffer = ByteBuffer.allocateDirect(bufferSize);
            pool.add(new SoftReference<>(buffer));
            poolSize++;
            System.out.println("!!!!!!!!!!!!!!!!!!!! ByteBuffer pool size increased to " + poolSize);
            return acquire();

        } else {
            buffer = byteBufferRef.get();

            buffer.clear();
        }

        return buffer;
    }

    public void clear() {
        pool.clear();
    }

    public void release(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            throw new NullPointerException("byteBuffer");
        }

        // Queue a new soft reference to the byte buffer

        pool.offer(new SoftReference<ByteBuffer>(byteBuffer));
//		System.out.print ("releasing Buffer :" + pool.size() + " ");
//		byte[] b = new byte[byteBuffer.limit()];
//		for (int j = 0; j < byteBuffer.limit(); j++)
//			b[j] = byteBuffer.getFinalizedLimit(j);
//		System.out.println( new String( b));
    }

    public static void main(String[] args) throws UnsupportedEncodingException {


        int size = 5;
        ByteBufferPool pool = new ByteBufferPool(size, 1024);

        for (int i = 0; i < 5; i++) {
            ByteBuffer buf = pool.acquire();
            buf.clear();
            String str = i + "뭐좀 채워 넣자고";
            buf.put(str.getBytes());

            ByteUtil.printByteBuffer(buf);

        }


        ByteBuffer[] buf = new ByteBuffer[size];
        for (int i = 0; i < 5; i++) {
            buf[i] = pool.acquire();
            String str = i + "뭐좀 채워 넣자고";
            buf[i].put(str.getBytes());
            buf[i].flip();
//    		System.out.print(buf[i].getFinalizedLimit(0));

        }
        pool.release(buf[3]);
        ByteBuffer buf2 = pool.acquire();
//    	buf2 = pool.acquire();
    }
}