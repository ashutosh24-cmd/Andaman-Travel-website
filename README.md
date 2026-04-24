# Andaman-Travel-website

# 🌴 Unfold Andaman - Travel Booking Platform

**Unfold Andaman** is a custom-built, full-stack Java web application designed to manage travel packages, user authentication, and bookings for trips to the Andaman Islands. 

This project was built from scratch without relying on heavy frameworks like Spring Boot. It uses Java's native `HttpServer` for backend routing and an in-memory Data Access Object (DAO) pattern for data management, making it incredibly lightweight and fast.

---

## ✨ Features

### Customer Features
* **User Authentication:** Secure registration and login system.
* **Browse Packages:** View detailed travel packages including pricing, duration, and destinations.
* **Booking System:** Customers can seamlessly book travel packages.
* **Dynamic UI:** Responsive frontend built with Vanilla HTML, CSS, and JS.

### Admin Features
* **Admin Dashboard:** A dedicated portal for administrators.
* **Package Management:** Add, update, or remove travel packages dynamically.
* **Booking Management:** View, approve, or reject customer bookings.

---

## 🛠️ Technology Stack

* **Frontend:** HTML5, CSS3, Vanilla JavaScript (Fetch API)
* **Backend:** Java 11+
* **Server:** Java Core `com.sun.net.httpserver.HttpServer` (No external web frameworks)
* **Data Storage:** In-Memory DataStore using Java Collections (Singleton DAO Pattern)
* **JSON Processing:** Custom manual JSON serialization and deserialization


# Project Structure 
Project Structure
```text Unfold Andaman/ ├── run.sh # Shell script to compile and run the Java server ├── webapp/ # Frontend Static Files │ ├── index.html # Main customer-facing website │ ├── admin.html # Admin management dashboard │ ├── css/ # Stylesheets │ ├── js/ # Client-side JavaScript │ └── images/ # Image assets └── src/main/java/com/travelgo/ # Backend Source Code ├── server/ # Entry point & HTTP Server configuration ├── models/ # Data Blueprints (User, Booking, TravelPackage) ├── dao/ # Data Access Layer (In-memory DataStore) ├── handlers/ # HTTP API Controllers (Auth, Packages, Bookings) └── service/ # External services (Email notifications) ```

# The Frontend (Client-Side)
The frontend is the visual part of the application that the user interacts with inside their web browser. It lives entirely inside the webapp/ folder.

Technologies Used:

HTML5: Provides the structure of the web pages (the text, images, and layout of the buttons).
CSS3: Provides the styling (colors, fonts, hover effects, and making the site look like a modern travel agency).
Vanilla JavaScript: Handles the dynamic logic. It makes the site interactive without needing to refresh the page.
Key Responsibilities:

User Interface (UI): It displays the beautiful travel packages, login forms, and the admin dashboard (index.html and admin.html).
Event Handling: It listens for user actions, such as clicking the "Book Now" button or typing a password into the login form.
Data Fetching: It uses the modern JavaScript fetch() API to send HTTP requests over the internet to the Backend to get data (like retrieving the list of packages) or send data (like submitting a new registration).
Dynamic Rendering: When the backend replies with data (e.g., a list of tours), the frontend JavaScript takes that data and instantly updates the HTML on the screen so the user can see it.
# The Backend (Server-Side)
The backend is the "brain" of the application. It runs on a computer server (or your terminal via ./run.sh) and handles all the heavy lifting, security, and data storage. It lives inside the src/main/java/ folder.

Technologies Used:

Core Java (JDK 11+): The programming language used to write all the logic.
Java HttpServer: A built-in Java library (com.sun.net.httpserver) used to listen for web traffic on port 8080.
In-Memory Data Structures: Java ArrayList and ConcurrentHashMap are used to act as the database.
Key Responsibilities:

Routing: The TravelGoServer.java listens to incoming web requests and acts like a traffic cop, deciding which "Handler" should deal with the request based on the URL (e.g., sending /api/auth to the AuthHandler).
Business Logic: It enforces the rules of the application. For example, the AuthHandler checks if an email is already registered before creating a new user, and the BookingHandler calculates the total price of a trip based on the number of travelers.
Data Management (DAO): The DataStore.java acts as the database. It safely stores all the users, packages, and bookings in the server's RAM so they can be retrieved instantly.
Serving Static Files: The StaticFileHandler takes the HTML/CSS/JS files from your webapp/ folder and physically sends them to the user's browser so they can view the website.
# The Communication Flow:
The Frontend and Backend are completely separate systems, but they talk to each other using an API (Application Programming Interface).

1) The Request: A user clicks "Register" on the Frontend. The JavaScript collects their name, email, and password, packages it into a text format called JSON (JavaScript Object Notation), and sends it to the Backend via an HTTP POST request.
2) The Processing: The Backend receives the JSON, translates it into a Java User object, saves it in the DataStore, and generates an "Account Created" success message.
3) The Response: The Backend converts the success message back into JSON and sends it back to the Frontend.
4) The Update: The Frontend receives the success response and uses JavaScript to show a green "Registration Successful!" pop-up to the user.
   
