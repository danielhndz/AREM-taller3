package edu.escuelaing.arem.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Request {

    private static final Logger LOGGER = Logger
            .getLogger(Request.class.getName());
    private String method;
    private String requestURI;
    private String httpVersion;
    private URI uri;
    private Map<String, String> query;
    private Map<String, String> headers;
    private String body;

    public Request(String requestLine) {
        parseRequestLine(requestLine);
    }

    public String getValFromQuery(String varname) {
        return query.get(varname);
    }

    public void addHeader(String k, String v) {
        if (headers != null) {
            headers.put(k, v);
        }
    }

    private void parseRequestLine(String requestLine) {
        try {
            String[] components = requestLine.split("\\s");
            method = components[0];
            requestURI = components[1];
            httpVersion = components[2];
            setUri(new URI(requestURI));
            query = parseQuery(uri.getQuery());
            headers = Collections.emptyMap();
            body = null;
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

    }

    private Map<String, String> parseQuery(String query) {
        if (query == null)
            return Collections.emptyMap();
        Map<String, String> mapedQuery = new HashMap<>();
        String[] nameValuePairs = query.split("&");
        for (String nameValuePair : nameValuePairs) {
            int index = nameValuePair.indexOf("=");
            if (index != -1) {
                mapedQuery.put(nameValuePair.substring(0, index), nameValuePair.substring(index + 1));
            }
        }
        return mapedQuery;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI theuri) {
        this.uri = theuri;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
