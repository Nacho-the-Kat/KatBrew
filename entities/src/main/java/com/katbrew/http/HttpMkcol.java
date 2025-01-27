package com.katbrew.http;

import org.apache.http.client.methods.HttpRequestBase;

import java.net.URI;

/**
 * Mkcol Http Methode.
 */
public class HttpMkcol extends HttpRequestBase {

    /**
     * Name der Http Methode.
     */
    private final static String METHOD_NAME = "MKCOL";

    /**
     * Konstruktor.
     */
    public HttpMkcol() {
        super();
    }

    /**
     * Konstruktor.
     *
     * @param uri Die Uri auf die der Request ausgeführt wird.
     */
    public HttpMkcol(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    /**
     * Liefert den Namen der Http Methode.
     *
     * @return Der Name der Http Methode.
     */
    @Override
    public String getMethod() {
        return METHOD_NAME;
    }


}
