package org.template;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Application {

    private static final int DEFAULT_PORT = 8000;
    private static final String PORT = "PORT";
    private static final String DELIMITER = "/";
    private static final String RESOURCE_DEV = "../Minimal-React-Starter/dist";
    private static final String INDEX = "index.html";
    private static final String API = "/API";

    private static int getPort() {
        final String port = System.getenv().get(PORT);
        if (port == null) return DEFAULT_PORT;
        return Integer.parseInt(port);
    }

    private static String getResource(final String[] args) {
        return args.length == 0? RESOURCE_DEV : args[0];
    }

    public static void main(final String[] args) throws Exception {
        final String resourcePath = getResource(args);
        final Server server = new Server(getPort());

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase(resourcePath);
        resourceHandler.setWelcomeFiles(new String[] {INDEX});
        final ContextHandler pageContextHandler = new ContextHandler(DELIMITER);
        pageContextHandler.setHandler(resourceHandler);

        final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath(DELIMITER);
        servletContextHandler.addServlet(new ServletHolder(new APIServlet()), API);

        final GzipHandler gzip = new GzipHandler();
        server.setHandler(gzip);
        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { pageContextHandler, servletContextHandler, new DefaultHandler() });
        gzip.setHandler(handlers);

        System.out.println("Server starting at " + getPort());
        server.start();
        server.join();
    }
}

class APIServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("{\"key\": \"value\"}");
    }
}
