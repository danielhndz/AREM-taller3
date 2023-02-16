package edu.escuelaing.arem.services.img;

import edu.escuelaing.arem.services.RestService;
import edu.escuelaing.arem.utils.FilesReader;

public class JpgService implements RestService {

    @Override
    public String getHeader() {
        return new StringBuilder("HTTP/1.1 200 OK\r\n")
                .append("Content-Type: image/jpg\r\n\r\n")
                .toString();
    }

    @Override
    public String getBody(String path) {
        return FilesReader.img(path);
    }

}
