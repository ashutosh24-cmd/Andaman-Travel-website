package com.travelgo.server;

import com.sun.net.httpserver.HttpServer;
import com.travelgo.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Main server class - Entry point for TravelGo application.
 * Uses core Java HttpServer (no external frameworks).
 */
public class TravelGoServer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API endpoints
        server.createContext("/api/packages", new PackageHandler());
        server.createContext("/api/bookings", new BookingHandler());
        server.createContext("/api/auth", new AuthHandler());

        // Static files
        String webRoot = System.getProperty("user.dir") + "/webapp";
        server.createContext("/", new StaticFileHandler(webRoot));

        server.setExecutor(null);
        server.start();

        System.out.println("===========================================");
        System.out.println("  Unfold Andaman Server started on port " + PORT);
        System.out.println("  Open http://localhost:" + PORT + " in browser");
        System.out.println("===========================================");
    }
}
