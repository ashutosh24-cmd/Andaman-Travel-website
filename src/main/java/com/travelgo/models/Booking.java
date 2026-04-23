package com.travelgo.models;

import java.time.LocalDate;

public class Booking {
    private int id;
    private String customerName;
    private String email;
    private String phone;
    private int packageId;
    private String travelDate;
    private int numTravelers;
    private double totalAmount;
    private String status;

    public Booking() {}

    public Booking(int id, String customerName, String email, String phone,
                   int packageId, String travelDate, int numTravelers, double totalAmount) {
        this.id = id;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.packageId = packageId;
        this.travelDate = travelDate;
        this.numTravelers = numTravelers;
        this.totalAmount = totalAmount;
        this.status = "CONFIRMED";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getPackageId() { return packageId; }
    public void setPackageId(int packageId) { this.packageId = packageId; }
    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }
    public int getNumTravelers() { return numTravelers; }
    public void setNumTravelers(int numTravelers) { this.numTravelers = numTravelers; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"customerName\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"," +
            "\"packageId\":%d,\"travelDate\":\"%s\",\"numTravelers\":%d,\"totalAmount\":%.2f,\"status\":\"%s\"}",
            id, escapeJson(customerName), escapeJson(email), escapeJson(phone),
            packageId, escapeJson(travelDate), numTravelers, totalAmount, escapeJson(status)
        );
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
