package bulls.tcp;

import java.nio.channels.SocketChannel;

public interface NBOnConnectHandler {
    void onConnect(SocketChannel sc) throws Exception;
}
