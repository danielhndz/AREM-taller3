package edu.escuelaing.arem.services.text;

import edu.escuelaing.arem.services.RestService;
import edu.escuelaing.arem.utils.FilesReader;

public class JsonService implements RestService {

    @Override
    public String getHeader() {
        return new StringBuilder("HTTP/1.1 200 OK\r\n")
                .append("Content-Type: application/json\r\n\r\n")
                .toString();
    }

    @Override
    public String getBody(String path) {
        return FilesReader.text(path);
    }

}
