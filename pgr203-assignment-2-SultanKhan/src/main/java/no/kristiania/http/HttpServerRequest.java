package no.kristiania.http;

import java.io.IOException;
import java.io.InputStream;

public class HttpServerRequest extends HttpMessage{

    public HttpServerRequest(InputStream inputStream) throws IOException {
        super(inputStream);
    }
}
