package no.kristiania.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {


    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpServer(0);
        server.start();
    }

    @Test
    void shouldReturnStatusCode200() throws IOException {
        server.start();
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo");
        assertEquals(200, client.execute().getStatusCode());
    }

    @Test
    void shouldReturnStatusCode401() throws IOException {
        HttpServer server = new HttpServer(0);
        server.start();
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?status=401");
        assertEquals(401, client.execute().getStatusCode());
    }

    @Test
    void shouldReturnHeaders() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?status=302&location=http://www.example.com");
        HttpClientResponse response = client.execute();
        assertEquals(302, response.getStatusCode());
        assertEquals("http://www.example.com", response.getHeader("location"));
    }

    @Test
    void shouldReturnBody() throws IOException {
        HttpClient client = new HttpClient("localhost", server.getPort(), "/echo?body=HelloWorld");
        assertEquals("HelloWorld", client.execute().getBody());
    }

    @Test
    void shouldReturnFileFromDisk() throws IOException {
        Files.writeString(Paths.get("target/mytestfile.txt"), "Hello Kristiania");
        server.setFileLocation("target");
        HttpClient client = new HttpClient("localhost", server.getPort(), "/mytestfile.txt");
        HttpClientResponse response = client.execute();
        assertEquals("Hello Kristiania", response.getBody());
    }

}