package com.travelgo.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.travelgo.dao.DataStore;
import com.travelgo.models.TravelPackage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all /api/packages/* requests.
 * Demonstrates: Polymorphism (implements HttpHandler interface)
 */
public class PackageHandler implements HttpHandler {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        String response;
        if ("GET".equals(method)) {
            if (path.matches("/api/packages/\\d+")) {
                int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                TravelPackage pkg = dataStore.getPackageById(id);
                if (pkg != null) {
                    response = pkg.toJson();
                } else {
                    response = "{\"error\":\"Package not found\"}";
                    sendResponse(exchange, 404, response);
                    return;
                }
            } else if (query != null && query.startsWith("search=")) {
                String searchTerm = java.net.URLDecoder.decode(query.substring(7), "UTF-8");
                List<TravelPackage> results = dataStore.searchPackages(searchTerm);
                response = "[" + results.stream().map(TravelPackage::toJson).collect(Collectors.joining(",")) + "]";
            } else {
                List<TravelPackage> allPkgs = dataStore.getAllPackages();
                response = "[" + allPkgs.stream().map(TravelPackage::toJson).collect(Collectors.joining(",")) + "]";
            }
            sendResponse(exchange, 200, response);
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
