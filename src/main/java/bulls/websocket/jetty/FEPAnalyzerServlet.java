package bulls.websocket.jetty;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class FEPAnalyzerServlet extends WebSocketServlet {
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(24L * 60 * 60 * 1000 * 365);
        factory.register(FEPAnalyzerSocket.class);
    }
}