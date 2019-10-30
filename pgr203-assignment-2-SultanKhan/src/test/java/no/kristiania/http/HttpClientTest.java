package no.kristiania.http;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientTest {

    private final String URLECHO = "urlecho.appspot.com";

    @Test
    void shouldExecuteRequests() throws IOException {
        HttpClient client = new HttpClient(URLECHO, 80, "/echo");
        assertEquals(200, client.execute().getStatusCode());
    }

    @Test
    void shouldReadStatusCode() throws IOException {
        HttpClient client = new HttpClient(URLECHO, 80, "/echo?status=401");
        assertEquals(401, client.execute().getStatusCode());
    }

    @Test
    void shouldReturnHeaders() throws IOException{
        HttpClient client = new HttpClient(URLECHO, 80, "/echo?content-type=text/plain");
        assertEquals("text/plain; charset=utf-8", client.execute().getHeader("Content-type"));
    }

    @Test
    void shouldReadBody() throws IOException {
        HttpClient client = new HttpClient(URLECHO, 80, "/echo?body=hello+world!");
        assertEquals("hello world!", client.execute().getBody());
    }

}