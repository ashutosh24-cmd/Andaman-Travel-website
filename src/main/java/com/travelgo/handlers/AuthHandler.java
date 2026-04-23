package com.travelgo.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.travelgo.dao.DataStore;
import com.travelgo.models.User;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Handles /api/auth/* requests (register, login).
 */
public class AuthHandler implements HttpHandler {
    private final DataStore dataStore = DataStore.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining());

        if (path.endsWith("/register") && "POST".equals(exchange.getRequestMethod())) {
            User user = new User();
            user.setName(extractJsonValue(body, "name"));
            user.setEmail(extractJsonValue(body, "email"));
            user.setPassword(extractJsonValue(body, "password"));
            User created = dataStore.registerUser(user);
            sendResponse(exchange, 201, created.toJson());
        } else if (path.endsWith("/login") && "POST".equals(exchange.getRequestMethod())) {
            String email = extractJsonValue(body, "email");
            String password = extractJsonValue(body, "password");
            User user = dataStore.loginUser(email, password);
            if (user != null) {
                sendResponse(exchange, 200, user.toJson());
            } else {
                sendResponse(exchange, 401, "{\"error\":\"Invalid email or password\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int start = idx + search.length();
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
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
