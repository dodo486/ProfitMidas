package bulls.tcp;

import bulls.exception.NoBidAskDataException;

public interface NBPacketHandler {
    String handle(byte[] packetFromClient) throws NoBidAskDataException;

}
