package edu.escuelaing.arem.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.escuelaing.arem.server.HttpServer;
import edu.escuelaing.arem.server.Request;
import edu.escuelaing.arem.services.RestService;
import edu.escuelaing.arem.services.img.IcoService;
import edu.escuelaing.arem.services.img.JpgService;
import edu.escuelaing.arem.services.img.PngService;
import edu.escuelaing.arem.services.text.CssService;
import edu.escuelaing.arem.services.text.HtmlService;
import edu.escuelaing.arem.services.text.JsService;
import edu.escuelaing.arem.services.text.JsonService;
import edu.escuelaing.arem.services.text.PlainService;

public class RequestProcessor implements Runnable {

    private static final Logger LOGGER = Logger
            .getLogger(RequestProcessor.class.getName());
    private static final String INDEX_PAGE = "/index.html";
    private final Socket clientSocket;

    private RequestProcessor(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public static RequestProcessor getAnInstance(Socket clientSocket) {
        return new RequestProcessor(clientSocket);
    }

    @Override
    public void run() {
        try {
            boolean bodyLines = false;
            String inputLine;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            Request request = null;
            while ((inputLine = in.readLine()) != null) {
                if (!in.ready()) {
                    break;
                } else if (inputLine.isBlank() && request != null) {
                    bodyLines = true;
                } else if (bodyLines) {
                    if (request.getBody() != null) {
                        request.setBody(request.getBody() + "\n" + inputLine);
                    } else {
                        request.setBody(inputLine);
                    }
                } else {
                    request = processLines(request, inputLine);
                }
            }
            if (request != null) {
                processRequestLine(request.getRequestURI());
            }
            in.close();
            clientSocket.close();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,
                    "\n\tServer side\n\tInterrupted!\n", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }

    private Request processLines(Request request, String inputLine) {
        if (request == null) {
            return new Request(inputLine);
        } else if (!inputLine.isBlank()) {
            return tryAddHeader(request, inputLine);
        }
        return request;
    }

    @SuppressWarnings({ "java:S1075" })
    private void processRequestLine(String inputLine) throws IOException {
        String path;
        String method = parseMethod(inputLine);
        if (!method.equals("")) {
            path = inputLine.replace(method + " ", "");
        } else {
            path = inputLine;
        }
        path = path.replace(" HTTP/1.0", "")
                .replace(" HTTP/1.1", "");
        if (path.toLowerCase().startsWith("/exit")) {
            exit();
        } else if (!processRequest(method, path) &&
                path.startsWith("/")) {
            LOGGER.log(Level.INFO, "\n\tPath:\n\n{0}\n", path);
            switch (path) {
                case "/":
                    path = INDEX_PAGE;
                    break;
                case "/favicon.ico":
                    path = "/favicon/favicon.ico";
                    break;
                default:
                    break;
            }
            processFile(path);
        }
    }

    private boolean processRequest(String method, String path) throws IOException {
        HttpServer server = HttpServer.getInstance();
        String route = path;
        if (path.contains("?")) {
            route = path.substring(0, path.indexOf("?"));
        }
        if (method.equals("GET") &&
                server.getGetRoutes().containsKey(route)) {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            out.write(server.getGetRoutes().get(route).apply(path));
            out.close();
            return true;
        } else if (method.equals("POST")
                && server.getPostRoutes().containsKey(route)) {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            out.write(server.getPostRoutes().get(route).apply(path));
            out.close();
            return true;
        }
        return false;
    }

    private void processFile(String path) throws IOException {
        String ext = getExtension(path);
        RestService restService = getRestServiceByExtension(ext);
        if (ext.equals("png") || ext.equals("jpg") || ext.equals("ico")) {
            if (Files.exists(Path.of("", (FilesReader.getResourcesDir() + path)
                    .replace(
                            "/",
                            System.getProperty("file.separator"))))) {
                DataOutputStream out = new DataOutputStream(
                        clientSocket.getOutputStream());
                out.writeBytes(restService.getHeader());
                out.write(
                        Base64.getDecoder().decode(restService.getBody(path)));
                out.close();
            } else {
                processFile(FilesReader.getNotFoundPage());
            }
        } else {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            out.write(
                    restService.getHeader()
                            + restService.getBody(path));
            out.close();
        }
    }

    private Request tryAddHeader(Request request, String inputLine) {
        String[] params;
        try {
            params = inputLine.split(":");
            if (params.length == 2) {
                params[0] = params[0].trim();
                params[1] = params[1].trim();
            }
        } catch (Exception e) {
            params = null;
        }
        if (params != null) {
            request.addHeader(params[0], params[1]);
        }
        return request;
    }

    private String getExtension(String path) {
        if (path.endsWith(".png")) {
            return "png";
        } else if (path.endsWith(".jpg")) {
            return "jpg";
        } else if (path.endsWith(".ico")) {
            return "ico";
        } else if (path.endsWith(".html")) {
            return "html";
        } else if (path.endsWith(".css")) {
            return "css";
        } else if (path.endsWith(".js")) {
            return "js";
        } else if (path.endsWith(".json")) {
            return "json";
        } else {
            return "plain";
        }
    }

    private RestService getRestServiceByExtension(String ext) {
        switch (ext) {
            case "png":
                return new PngService();
            case "jpg":
                return new JpgService();
            case "ico":
                return new IcoService();
            case "html":
                return new HtmlService();
            case "css":
                return new CssService();
            case "js":
                return new JsService();
            case "json":
                return new JsonService();
            default:
                return new PlainService();
        }
    }

    private void exit() {
        try (PrintWriter in = new PrintWriter(
                clientSocket.getOutputStream(), true)) {
            StringBuilder response = new StringBuilder("HTTP/1.1 200 OK\r\n")
                    .append("Content-Type: text/plain\r\n\r\n")
                    .append("\tClient side\n\tStopping server ...");
            in.println(response);
            HttpServer.getInstance().stop();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error stopping server.");
            e.printStackTrace();
        }
    }

    private String parseMethod(String inputLine) {
        if (inputLine.startsWith("GET")) {
            return "GET";
        } else if (inputLine.startsWith("POST")) {
            return "POST";
        }
        return "";
    }
}
