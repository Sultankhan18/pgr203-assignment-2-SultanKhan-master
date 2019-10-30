package no.kristiania.http;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private static ServerSocket serverSocket;
    private String fileLocation;


    public HttpServer(int port) throws IOException {

        serverSocket = new ServerSocket(port);
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8080);
        server.setFileLocation("src/main/resources");
        server.start();
    }

    void start() {
        new Thread(this::run).start();

    }

    private void run() {
        while (true) {
            try (Socket socket = serverSocket.accept()) {
                HttpServerRequest request = new HttpServerRequest(socket.getInputStream());
                String requestLine = request.getStartLine();
                String requestTarget = requestLine.split(" ")[1];

                int questionPos = requestTarget.indexOf('?');

                Map<String, String> requestParameter = parseRequestParameters(requestTarget, socket);
                String requestPath = questionPos == -1 ? requestTarget : requestTarget.substring(0, questionPos);

                if (!requestPath.equals("/echo")) {
                    File file = new File(fileLocation + requestPath);
                    if(file.exists()){
                        socket.getOutputStream().write(("HTTP/1.1 200 OK\r\n" +
                                "Content-Length: " + file.length() + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n").getBytes());
                        new FileInputStream(file).transferTo(socket.getOutputStream());

                    } else {
                        socket.getOutputStream().write(("HTTP/1.1 404 NOT FOUND\r\n" +
                                "Connection: close\r\n" +
                                "\r\n").getBytes());
                    }
                } else {
                    String statusCode = requestParameter.getOrDefault("status", "200");
                    String location = requestParameter.get("location");
                    String body = requestParameter.getOrDefault("body", "Hello world!");

                    socket.getOutputStream().write(("HTTP/1.0 " + statusCode + " OK\r\n" +
                            "Content-length: " + body.length() + "\r\n" +
                            (location != null ? "Location: " + location + "\r\n" : "") +
                            "\r\n" +
                            body).getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private Map<String, String> parseRequestParameters(String requestTarget, Socket socket) throws IOException {
        Map<String, String> requestParameter = new HashMap<>();

        int questionPos = requestTarget.indexOf('?');

        if (questionPos != -1) {

            String query = requestTarget.substring(questionPos + 1);
            for (String parameter : query.split("&")) {
                int equalPos = parameter.indexOf('=');
                String parameterValue = parameter.substring(equalPos + 1);
                String parameterName = parameter.substring(0, equalPos);
                requestParameter.put(parameterName, parameterValue);
            }
        }

        return requestParameter;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
