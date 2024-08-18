package bulls.websocket.handler;

import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.dmaLog.marketshare.MarketTradingValueShareDataProvider;
import bulls.dmaLog.marketshare.MarketVolumeShareDataProvider;
import bulls.dmaLog.transactiontracker.TransactionTrackDataProvider;
import bulls.json.DefaultMapper;
import bulls.json.JsonKey;
import bulls.json.JsonValue;
import bulls.log.DefaultLogger;
import bulls.staticData.PredefinedString;
import bulls.websocket.WebSocketSessionCallbackMessageSender;
import bulls.websocket.asyncHandling.AsyncResponse;
import bulls.websocket.asyncHandling.AsyncResponseCenter;
import bulls.websocket.session.WebsocketSession;

import java.io.IOException;

public class FEPAnalyzerWSPacketHandler implements WebSocketPacketHandler {
    @Override
    public String handle(WebsocketSession session, byte[] packetFromClient) {

        String str = new String(packetFromClient);
        return handle(session, str);
    }

    @Override
    public String handle(WebsocketSession session, String utfJSON) {
        String response = PredefinedString.NO_RESPONSE;
        try {
            ObjectNode node = (ObjectNode) DefaultMapper.getMapper().readTree(utfJSON);
            String trCode = node.get(JsonKey.TRCODE).asText();

            DefaultLogger.logger.debug("packet from client[{}]", utfJSON);

            switch (trCode) {
                case JsonValue.TRCODE_FEP_TRANSACTION_TRACK:
                    AsyncResponseCenter.Instance.work(new AsyncResponse(session, node),
                            TransactionTrackDataProvider.class,
                            WebSocketSessionCallbackMessageSender.class);
                    break;
                case JsonValue.TRCODE_FEP_TODAY_TOTAL_QUANTITY:
                    AsyncResponseCenter.Instance.work(new AsyncResponse(session, node),
                            MarketVolumeShareDataProvider.class,
                            WebSocketSessionCallbackMessageSender.class);
                    break;
                case JsonValue.TRCODE_FEP_TODAY_TOTAL_PRICE:
                    AsyncResponseCenter.Instance.work(new AsyncResponse(session, node),
                            MarketTradingValueShareDataProvider.class,
                            WebSocketSessionCallbackMessageSender.class);
                    break;
                default:
                    break;
            }

        } catch (IOException | IllegalStateException e) {
            DefaultLogger.logger.error("error found", e);
            System.out.println(utfJSON);
        } catch (NullPointerException e) {
            if (utfJSON != null) {
                DefaultLogger.logger.error("WS {} 패킷으로부터 파싱 에러 발생!!! ", utfJSON);
            }
            DefaultLogger.logger.error("error found", e);
            System.out.println(utfJSON);
        }
        return response;
    }
}
