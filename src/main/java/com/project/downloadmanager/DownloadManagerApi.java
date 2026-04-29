package com.project.downloadmanager;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import javafx.application.Platform;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class DownloadManagerApi {

    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/download/add", new DownloadHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server is running on http://localhost:8080");
    }

    static class DownloadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();

                if (query != null && query.startsWith("url=")) {
                    String url = query.substring(4);
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        System.out.println("Download URL received: " + url);

                        Platform.runLater(() -> {
                            DownloadManagerApplication app = DownloadManagerApplication.getInstance();
                            app.getUrlForDownloadFromExtension(url);
                            System.out.println("Processing URL on JavaFX thread: " + url);
                        });

                        String response = url;
                        exchange.sendResponseHeaders(200, response.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else {
                        sendErrorResponse(exchange, "Invalid URL. Only HTTP/HTTPS are supported.");
                    }
                } else {
                    sendErrorResponse(exchange, "Missing 'url' parameter.");
                }
            } else {
                sendErrorResponse(exchange, "Invalid request method. Use POST.");
            }
        }

        private void sendErrorResponse(HttpExchange exchange, String errorMessage) throws IOException {
            exchange.sendResponseHeaders(400, errorMessage.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(errorMessage.getBytes());
            }
        }
    }
}
