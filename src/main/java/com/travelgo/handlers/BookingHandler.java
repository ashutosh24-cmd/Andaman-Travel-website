package com.travelgo.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.travelgo.dao.DataStore;
import com.travelgo.models.Booking;
import com.travelgo.models.TravelPackage;
import com.travelgo.service.EmailService;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles /api/bookings requests including creation, listing, and cancellation.
 * Demonstrates: Abstraction, Encapsulation, Polymorphism (HttpHandler)
 */
public class BookingHandler implements HttpHandler {
    private final DataStore dataStore = DataStore.getInstance();
    private final EmailService emailService = EmailService.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        // POST /api/bookings - Create new booking
        if ("POST".equals(method) && path.equals("/api/bookings")) {
            String body = readBody(exchange);
            Booking booking = parseBooking(body);
            Booking created = dataStore.createBooking(booking);

            // Send emails in a background thread so response is not delayed
            TravelPackage pkg = dataStore.getPackageById(created.getPackageId());
            if (pkg != null) {
                final Booking b = created;
                new Thread(() -> {
                    String bookingRef = "UA" + String.format("%05d", b.getId());
                    // 1. Send confirmation email to customer
                    emailService.sendBookingConfirmation(
                        b.getCustomerName(), b.getEmail(), bookingRef,
                        pkg.getName(), pkg.getDestination(), b.getTravelDate(),
                        b.getNumTravelers(), b.getTotalAmount(),
                        pkg.getHotelName(), pkg.getCabType(),
                        pkg.getTicketDetails(), pkg.getLocalTourInfo(), pkg.getDurationDays()
                    );
                    // 2. Send notification to admin (ashutoshvarma393@gmail.com)
                    emailService.sendAdminBookingNotification(
                        b.getCustomerName(), b.getEmail(), b.getPhone(), bookingRef,
                        pkg.getName(), b.getTravelDate(), b.getNumTravelers(), b.getTotalAmount()
                    );
                }).start();
            }

            sendResponse(exchange, 201, created.toJson());
            return;
        }

        // POST /api/bookings/{id}/cancel - Cancel booking
        if ("POST".equals(method) && path.matches("/api/bookings/\\d+/cancel")) {
            String idStr = path.replaceAll("/api/bookings/(\\d+)/cancel", "$1");
            int id = Integer.parseInt(idStr);
            Booking booking = dataStore.getBookingById(id);

            if (booking == null) {
                sendResponse(exchange, 404, "{\"error\":\"Booking not found\"}");
                return;
            }
            if ("CANCELLED".equals(booking.getStatus())) {
                sendResponse(exchange, 400, "{\"error\":\"Booking is already cancelled\"}");
                return;
            }

            // Cancel the booking
            booking.setStatus("CANCELLED");
            double refundAmount = booking.getTotalAmount();

            // Send cancellation emails
            TravelPackage pkg = dataStore.getPackageById(booking.getPackageId());
            String pkgName = pkg != null ? pkg.getName() : "Package #" + booking.getPackageId();
            final Booking b = booking;
            new Thread(() -> {
                String bookingRef = "UA" + String.format("%05d", b.getId());
                // 1. Send cancellation confirmation to customer
                emailService.sendCancellationConfirmation(
                    b.getCustomerName(), b.getEmail(), bookingRef, pkgName, refundAmount
                );
                // 2. Notify admin about cancellation
                emailService.sendAdminCancellationNotification(
                    b.getCustomerName(), b.getEmail(), bookingRef, pkgName, refundAmount
                );
            }).start();

            sendResponse(exchange, 200,
                "{\"message\":\"Booking cancelled successfully\",\"bookingId\":" + id
                + ",\"status\":\"CANCELLED\",\"refundAmount\":" + refundAmount + "}");
            return;
        }

        // GET /api/bookings/{id}
        if ("GET".equals(method) && path.matches("/api/bookings/\\d+")) {
            int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
            Booking b = dataStore.getBookingById(id);
            if (b != null) {
                sendResponse(exchange, 200, b.toJson());
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Booking not found\"}");
            }
            return;
        }

        // GET /api/bookings
        if ("GET".equals(method)) {
            List<Booking> all = dataStore.getAllBookings();
            String json = "[" + all.stream().map(Booking::toJson).collect(Collectors.joining(",")) + "]";
            sendResponse(exchange, 200, json);
            return;
        }

        sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining());
    }

    private Booking parseBooking(String json) {
        Booking b = new Booking();
        b.setCustomerName(extractJsonValue(json, "customerName"));
        b.setEmail(extractJsonValue(json, "email"));
        b.setPhone(extractJsonValue(json, "phone"));
        b.setPackageId(Integer.parseInt(extractJsonValue(json, "packageId")));
        b.setTravelDate(extractJsonValue(json, "travelDate"));
        b.setNumTravelers(Integer.parseInt(extractJsonValue(json, "numTravelers")));
        return b;
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
