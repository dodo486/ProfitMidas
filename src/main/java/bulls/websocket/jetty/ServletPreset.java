package bulls.websocket.jetty;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;

public interface ServletPreset {
    Class<? extends WebSocketServlet> getServletClass();
    String getServletName();
    String getPath();
}
