package me.victor.npaw;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Main {

    private static final int port = 8090;
    private static Server server;
    public static URI serverUri;
    public static PrintWriter writer;

    public Main(){

    }

    public static void main(String[] args) throws Exception {

        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});

        ServletHandler servletHandler = new ServletHandler();
        server.setHandler(servletHandler);

        servletHandler.addServletWithMapping(HelloServlet.class, "/");

        server.start();
        serverUri = new URI(String.format("http://localhost:%d/",port));
        //server.join();
        writer =  new PrintWriter("log.txt", "UTF-8");

    }

    public static void end() throws Exception {
        server.stop();
        writer.close();
    }


    public static class HelloServlet extends HttpServlet
    {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            /*
            Map<String, String[]> rp = request.getParameterMap();
            for(Map.Entry<String,String[]> entry : rp.entrySet()){
                System.out.println(entry.getKey());
                System.out.println(entry.getValue()[0]);


            }

            //System.out.println(request.getParameter("name"));

            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("New Hello Simple Servlet");*/



            ByteBuffer content = ByteBuffer.wrap(
                    HEAVY_RESOURCE.getBytes(StandardCharsets.UTF_8));

            AsyncContext async = request.startAsync();
            String name = async.getRequest().getParameter("name");
            //writer.write(name+"\n");

            ServletOutputStream out = response.getOutputStream();
            out.setWriteListener(new WriteListener() {
                @Override
                public void onWritePossible() throws IOException {
                    while (out.isReady()) {
                        if (!content.hasRemaining()) {
                            response.setStatus(200);
                            async.complete();
                            return;
                        }
                        out.write(content.get());
                    }
                }

                @Override
                public void onError(Throwable t) {
                    getServletContext().log("Async Error", t);
                    async.complete();
                }
            });
        }
    }

    private static String HEAVY_RESOURCE
            = "This is some heavy resource that will be served in an async way";
}

