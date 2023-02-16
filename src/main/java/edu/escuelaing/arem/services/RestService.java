package edu.escuelaing.arem.services;

public interface RestService {

    static final String RESOURCES_DIR = "src/main/resources";

    public String getHeader();

    public String getBody(String path);
}