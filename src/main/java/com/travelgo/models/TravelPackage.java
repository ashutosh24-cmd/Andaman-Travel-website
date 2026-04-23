package com.travelgo.models;

/**
 * TravelPackage - Represents a travel package offered by Unfold Andaman.
 * 
 * Demonstrates:
 *   - Encapsulation (private fields with public getters/setters)
 *   - Constructor Overloading (default + parameterized constructors)
 *   - Object-Oriented Modeling (real-world travel package as a Java class)
 * 
 * Each package includes details about destination, pricing, duration,
 * hotel accommodation, cab service, tickets, and local tour information.
 * 
 * @author Unfold Andaman Team
 * @version 1.0
 */
public class TravelPackage {
    /** Unique identifier for this package */
    private int id;
    /** Display name of the travel package */
    private String name;
    /** Travel destination (e.g., "Andaman & Nicobar Islands") */
    private String destination;
    /** Brief description of the package experience */
    private String description;
    /** Price per person in INR */
    private double price;
    /** Trip duration in days */
    private int durationDays;
    /** Name of the included hotel */
    private String hotelName;
    /** Type of cab service included (e.g., "AC Sedan") */
    private String cabType;
    /** Details about included tickets (flights, ferry, etc.) */
    private String ticketDetails;
    /** Information about included local tours and activities */
    private String localTourInfo;
    /** URL/path to the package's display image */
    private String imageUrl;
    /** Average customer rating (out of 5.0) */
    private double rating;

    /** Default constructor — creates an empty TravelPackage */
    public TravelPackage() {}

    /**
     * Parameterized constructor — creates a TravelPackage with all details.
     * 
     * @param id             unique package identifier
     * @param name           package display name
     * @param destination    travel destination
     * @param description    brief description
     * @param price          price per person in INR
     * @param durationDays   trip duration in days
     * @param hotelName      included hotel name
     * @param cabType        included cab type
     * @param ticketDetails  included ticket information
     * @param localTourInfo  local tour details
     * @param imageUrl       image URL/path
     * @param rating         average rating (0.0 - 5.0)
     */
    public TravelPackage(int id, String name, String destination, String description,
                         double price, int durationDays, String hotelName, String cabType,
                         String ticketDetails, String localTourInfo, String imageUrl, double rating) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.description = description;
        this.price = price;
        this.durationDays = durationDays;
        this.hotelName = hotelName;
        this.cabType = cabType;
        this.ticketDetails = ticketDetails;
        this.localTourInfo = localTourInfo;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getCabType() { return cabType; }
    public void setCabType(String cabType) { this.cabType = cabType; }
    public String getTicketDetails() { return ticketDetails; }
    public void setTicketDetails(String ticketDetails) { this.ticketDetails = ticketDetails; }
    public String getLocalTourInfo() { return localTourInfo; }
    public void setLocalTourInfo(String localTourInfo) { this.localTourInfo = localTourInfo; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    /**
     * Converts this TravelPackage object to a JSON string.
     * Uses manual String.format instead of external JSON libraries.
     * 
     * @return JSON string representation of this travel package
     */
    public String toJson() {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"destination\":\"%s\",\"description\":\"%s\"," +
            "\"price\":%.2f,\"durationDays\":%d,\"hotelName\":\"%s\",\"cabType\":\"%s\"," +
            "\"ticketDetails\":\"%s\",\"localTourInfo\":\"%s\",\"imageUrl\":\"%s\",\"rating\":%.1f}",
            id, escapeJson(name), escapeJson(destination), escapeJson(description),
            price, durationDays, escapeJson(hotelName), escapeJson(cabType),
            escapeJson(ticketDetails), escapeJson(localTourInfo), escapeJson(imageUrl), rating
        );
    }

    /**
     * Escapes special characters for safe JSON string output.
     * Handles backslashes, double quotes, and newlines.
     * 
     * @param s the string to escape
     * @return escaped string safe for JSON embedding, or empty string if null
     */
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
