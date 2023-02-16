package edu.escuelaing.arem;

import edu.escuelaing.arem.server.HttpServer;
import edu.escuelaing.arem.server.Request;
import edu.escuelaing.arem.services.RestService;
import edu.escuelaing.arem.services.text.HtmlService;
import edu.escuelaing.arem.services.text.PlainService;
import edu.escuelaing.arem.utils.FilesReader;

public class Launcher {

    private static final String GREETING = "Respondiendo desde un archivo plano." +
            "\nNombre: Daniel Felipe Hernandez Mancipe.";

    @SuppressWarnings("java:S1612")
    public static void main(String[] args) {
        HttpServer server = HttpServer.getInstance();
        if (args != null && args.length > 0) {
            try {
                server.setMaxThreads(Integer.valueOf(args[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        server.get("/lab3v1", (Request path) -> lab3v1(path));
        server.get("/lab3v2", Launcher::lab3v1);
        server.get("/lab3v3", Launcher::lab3v2);
        server.post("/lab3v1", (Request path) -> lab3v2(path));
        server.post("/lab3v2", Launcher::lab3v1);
        server.post("/lab3v3", Launcher::lab3v2);
        server.get("/setResDir", Launcher::setResDir);
        server.post("/setResDir", Launcher::setResDir);
        server.start();
    }

    private static String setResDir(Request request) {
        String path = request.getRequestURI();
        RestService restService = new PlainService();
        int i = path.indexOf("?");
        if (i != -1) {
            String[] params = path.substring(i + 1).split("&");
            for (String p : params) {
                if (p.startsWith("resourcesDir=")) {
                    FilesReader.setResourcesDir(p.replace("resourcesDir=", ""));
                    return restService.getHeader() +
                            GREETING +
                            "\n\nSe configuro exitosamente el directorio de " +
                            "donde se leeran los archivos estaticos";
                }
            }
        }
        return restService.getHeader() +
                GREETING +
                "\n\nNo se modificó el directorio de archivos estaticos";

    }

    private static String lab3v1(Request request) {
        String path = request.getRequestURI();
        RestService restService = new PlainService();
        int i = path.indexOf("?");
        StringBuilder stringBuilder = new StringBuilder("\n");
        if (i != -1) {
            String[] params = path.substring(i + 1).split("&");
            for (String p : params) {
                stringBuilder.append("\n" + p);
            }
        }
        return restService.getHeader() +
                GREETING +
                "\n\nNota: No pongo la tilde a la 'a' en 'Hernandez', " +
                "porque al ser archivo plano el browser no renderiza " +
                "bien las tildes." + stringBuilder;
    }

    @SuppressWarnings("java:S1172")
    private static String lab3v2(Request request) {
        RestService restService = new HtmlService();
        return restService.getHeader() +
                restService.getBody("/name/name.html");
    }
}
