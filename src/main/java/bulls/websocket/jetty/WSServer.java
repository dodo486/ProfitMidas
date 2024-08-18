package bulls.websocket.jetty;


import bulls.thread.GeneralCoreThreadFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;

public class WSServer {
    Server server;
    ServerConnector connector;
    ServletContextHandler context;

    public WSServer(String ip, int port) {
        int minThread = 20;
        int maxThread = 2000;
        int capacity = Math.max(minThread, 8) * 1024;
        QueuedThreadPool qtp = new QueuedThreadPool(maxThread, minThread, 24 * 60 * 60 * 1000, -1, new BlockingArrayQueue<Runnable>(capacity, capacity), null, new GeneralCoreThreadFactory());
        server = new Server(qtp);
        connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setHost(ip);
        server.addConnector(connector);
        context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
    }

    public void setServer(Class<? extends WebSocketServlet> servlet, String servletName, String path) {
        ServletHolder holderEvents = new ServletHolder(servletName, servlet);
        context.addServlet(holderEvents, "/" + path + "/*");
    }

    public void setServer(ServletPreset preset) {
        setServer(preset.getServletClass(), preset.getServletName(), preset.getPath());
    }

    public void start() {
        try {
            server.start();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    public void waitTillFinish() {
        try {
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}