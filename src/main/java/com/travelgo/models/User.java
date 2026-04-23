package com.travelgo.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String joinedDate;
    private String role; // "CUSTOMER" or "ADMIN"

    public User() {
        this.joinedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.role = "CUSTOMER";
    }

    public User(int id, String name, String email, String password) {
        this();
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getJoinedDate() { return joinedDate; }
    public void setJoinedDate(String joinedDate) { this.joinedDate = joinedDate; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"address\":\"%s\",\"joinedDate\":\"%s\",\"role\":\"%s\"}",
            id, esc(name), esc(email), esc(phone), esc(address), esc(joinedDate), esc(role)
        );
    }

    public String toJsonWithToken(String token) {
        return String.format(
            "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"address\":\"%s\",\"joinedDate\":\"%s\",\"role\":\"%s\",\"token\":\"%s\"}",
            id, esc(name), esc(email), esc(phone), esc(address), esc(joinedDate), esc(role), esc(token)
        );
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
