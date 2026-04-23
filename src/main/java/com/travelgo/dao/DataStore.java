package com.travelgo.dao;

import com.travelgo.models.TravelPackage;
import com.travelgo.models.Booking;
import com.travelgo.models.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory Data Access Object - simulates a database using collections.
 * Demonstrates OOP concepts: Encapsulation, Singleton Pattern.
 */
public class DataStore {
    private static DataStore instance;
    private final List<TravelPackage> packages;
    private final List<Booking> bookings;
    private final List<User> users;
    private final Map<String, Integer> sessions; // token -> userId
    private final AtomicInteger bookingIdCounter = new AtomicInteger(1);
    private final AtomicInteger userIdCounter = new AtomicInteger(1);

    private DataStore() {
        packages = new ArrayList<>();
        bookings = Collections.synchronizedList(new ArrayList<>());
        users = Collections.synchronizedList(new ArrayList<>());
        sessions = new ConcurrentHashMap<>();
        initializePackages();
        initializeAdmin();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void initializeAdmin() {
        User admin = new User();
        admin.setId(userIdCounter.getAndIncrement());
        admin.setName("Admin");
        admin.setEmail("admin@unfoldandaman.com");
        admin.setPassword("admin123");
        admin.setPhone("+91 98765 43210");
        admin.setRole("ADMIN");
        users.add(admin);
    }

    private void initializePackages() {
        packages.add(new TravelPackage(1, "Andaman Beach Paradise",
            "Andaman & Nicobar Islands",
            "Explore the pristine beaches of Havelock Island, enjoy snorkeling at Elephant Beach, and witness the stunning sunsets at Radhanagar Beach. This all-inclusive package covers everything from airport transfers to island hopping.",
            45999.00, 5,
            "Taj Exotica Resort & Spa", "AC Sedan (Innova Crysta)",
            "Return flights + Ferry tickets to Havelock & Neil Island",
            "Cellular Jail visit, Ross Island tour, Coral reef snorkeling, Mangrove kayaking",
            "images/beach.webp", 4.8));

        packages.add(new TravelPackage(2, "Heritage Fort Explorer",
            "Port Blair Heritage Trail",
            "Dive deep into the rich history of the Andaman Islands. Visit the iconic Cellular Jail, explore the remnants of British colonial architecture, and experience the Light & Sound show that brings history alive.",
            38499.00, 4,
            "Fortune Resort Bay Island", "AC SUV (Fortuner)",
            "Return flights + Local ferry passes",
            "Cellular Jail, Anthropological Museum, Chatham Saw Mill, Corbyn's Cove Beach",
            "images/monument.jpeg", 4.6));

        packages.add(new TravelPackage(3, "Tropical Island Hopper",
            "Neil Island & Baratang",
            "A perfect blend of adventure and relaxation. Visit the natural bridge at Neil Island, explore the limestone caves of Baratang, and relax on untouched beaches with crystal clear waters.",
            52999.00, 6,
            "SeaShell Havelock", "AC Tempo Traveller",
            "Return flights + Speed boat transfers",
            "Natural Bridge, Limestone caves, Mud volcano, Parrot Island sunset cruise",
            "images/beach.webp", 4.9));

        packages.add(new TravelPackage(4, "Romantic Getaway",
            "Havelock Island",
            "The ultimate couples retreat. Enjoy candlelit dinners on the beach, private snorkeling sessions, couple spa treatments, and a private sunset cruise around the islands.",
            65999.00, 5,
            "Munjoh Ocean Resort", "Private AC Car",
            "Return flights + Private speedboat",
            "Private beach dinner, Couple spa, Sunset cruise, Scuba diving for two",
            "images/monument.jpeg", 4.7));

        packages.add(new TravelPackage(5, "Adventure Seeker",
            "Andaman Adventure Circuit",
            "For the thrill seekers! Experience scuba diving, sea walking, jet skiing, parasailing, and deep sea fishing. Includes professional training and all safety equipment.",
            58499.00, 7,
            "Symphony Palms Beach Resort", "AC Van",
            "Return flights + Inter-island ferry",
            "Scuba diving, Sea walking, Jet ski, Parasailing, Deep sea fishing, Night kayaking",
            "images/beach.webp", 4.5));

        packages.add(new TravelPackage(6, "Budget Explorer",
            "Andaman Essentials",
            "Experience the best of Andaman without breaking the bank. Covers all must-visit spots including Cellular Jail, Radhanagar Beach, and a half-day snorkeling trip.",
            28999.00, 3,
            "Hotel Driftwood", "Shared AC Bus",
            "Return flights included",
            "Cellular Jail, Radhanagar Beach, Glass bottom boat ride, North Bay coral viewing",
            "images/monument.jpeg", 4.3));
    }

    // ── Package Methods ──
    public List<TravelPackage> getAllPackages() {
        return new ArrayList<>(packages);
    }

    public TravelPackage getPackageById(int id) {
        return packages.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public List<TravelPackage> searchPackages(String query) {
        String lower = query.toLowerCase();
        return packages.stream()
            .filter(p -> p.getName().toLowerCase().contains(lower) ||
                         p.getDestination().toLowerCase().contains(lower) ||
                         p.getDescription().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }

    // ── Booking Methods ──
    public Booking createBooking(Booking booking) {
        booking.setId(bookingIdCounter.getAndIncrement());
        booking.setStatus("CONFIRMED");
        TravelPackage pkg = getPackageById(booking.getPackageId());
        if (pkg != null) {
            booking.setTotalAmount(pkg.getPrice() * booking.getNumTravelers());
        }
        bookings.add(booking);
        return booking;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public Booking getBookingById(int id) {
        return bookings.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    public List<Booking> getBookingsByEmail(String email) {
        return bookings.stream()
            .filter(b -> b.getEmail().equalsIgnoreCase(email))
            .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByUserId(int userId) {
        User user = getUserById(userId);
        if (user == null) return new ArrayList<>();
        return getBookingsByEmail(user.getEmail());
    }

    // ── User Methods ──
    public User registerUser(User user) {
        // Check if email already exists
        if (getUserByEmail(user.getEmail()) != null) {
            return null; // duplicate
        }
        user.setId(userIdCounter.getAndIncrement());
        user.setRole("CUSTOMER");
        users.add(user);
        return user;
    }

    public User loginUser(String email, String password) {
        return users.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password))
            .findFirst().orElse(null);
    }

    public User getUserById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    public User getUserByEmail(String email) {
        return users.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email))
            .findFirst().orElse(null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public List<User> getAllCustomers() {
        return users.stream()
            .filter(u -> "CUSTOMER".equals(u.getRole()))
            .collect(Collectors.toList());
    }

    public User updateUser(int id, String name, String phone, String address) {
        User user = getUserById(id);
        if (user != null) {
            if (name != null && !name.isEmpty()) user.setName(name);
            if (phone != null) user.setPhone(phone);
            if (address != null) user.setAddress(address);
        }
        return user;
    }

    public boolean deleteUser(int id) {
        return users.removeIf(u -> u.getId() == id && "CUSTOMER".equals(u.getRole()));
    }

    // ── Session Methods ──
    public String createSession(int userId) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, userId);
        return token;
    }

    public User getUserByToken(String token) {
        if (token == null || token.isEmpty()) return null;
        Integer userId = sessions.get(token);
        if (userId == null) return null;
        return getUserById(userId);
    }

    public void removeSession(String token) {
        sessions.remove(token);
    }
}
