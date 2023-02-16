package edu.escuelaing.arem.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.escuelaing.arem.utils.RequestProcessor;

public class HttpServer implements Runnable {

    private static final Logger LOGGER = Logger
            .getLogger(HttpServer.class.getName());
    private static Map<String, Function<String, String>> getRoutes = new HashMap<>();
    private static Map<String, Function<String, String>> postRoutes = new HashMap<>();
    private static final int PORT = 35000;
    private static HttpServer instance;
    private ExecutorService pool;
    private ServerSocket serverSocket;
    private int maxThreads = 8;
    private boolean running;

    private HttpServer() {
    }

    public static HttpServer getInstance() {
        if (instance == null) {
            instance = new HttpServer();
        }
        return instance;
    }

    @SuppressWarnings({ "java:S2189", "java:S2589", "java:S125" })
    public void start() {
        Socket clientSocket = null;
        pool = Executors.newFixedThreadPool(maxThreads);
        serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            String msg = MessageFormat.format(
                    "\n\tServer side" +
                            "\n\tServer socket created" +
                            "\n\tmaxThreads {0}\n",
                    maxThreads);
            LOGGER.log(Level.INFO, msg);
            running = true;
        } catch (IOException e) {
            LOGGER.log(Level.INFO,
                    "\n\tServer side\n\t" +
                            "Could not listen on port: {0}\n",
                    PORT);
            System.exit(1);
        }

        while (running) {
            try {
                clientSocket = serverSocket.accept();
                LOGGER.log(Level.INFO,
                        "\n\tServer side\n\tSocket {0} accepted\n",
                        clientSocket.hashCode());
                pool.execute(RequestProcessor.getAnInstance(clientSocket));
            } catch (IOException e) {
                /*
                 * e.printStackTrace();
                 * LOGGER.log(Level.INFO, "\n\tServer side\n\tAccept failed\n");
                 */
            }
        }

        exit();
    }

    @Override
    public void run() {
        start();
    }

    @SuppressWarnings("java:S4276")
    public void get(String endpoint, Function<String, String> handler) {
        getRoutes.put(endpoint, handler);
    }

    @SuppressWarnings("java:S4276")
    public void post(String endpoint, Function<String, String> handler) {
        postRoutes.put(endpoint, handler);
    }

    public synchronized void stopWithoutExit() {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(15, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                if (!pool.awaitTermination(15, TimeUnit.SECONDS)) {
                    LOGGER.log(Level.WARNING, "\n\tPool did not terminate\n");
                }
            }
        } catch (InterruptedException e) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void stop() {
        stopWithoutExit();
        exit();
    }

    private void exit() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                LOGGER.log(Level.INFO, "\n\tServer socket closed\n");
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "\n\tClose server failed\n");
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        if (!running) {
            this.maxThreads = maxThreads;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public Map<String, Function<String, String>> getGetRoutes() {
        return getRoutes;
    }

    public Map<String, Function<String, String>> getPostRoutes() {
        return postRoutes;
    }
}