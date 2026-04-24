# Andaman-Travel-website

# File structure 

Unfold Andaman/                 <-- The Root Project Folder
│
├── run.sh                      <-- The bash script used to compile and start the server
├── email.properties            <-- Configuration file for your email service (SMTP settings)
│
├── lib/                        <-- (Directory) Put external .jar libraries here (if any)
├── out/                        <-- (Directory) Auto-generated when you run the server; holds compiled .class files
│
├── webapp/                     <-- (Directory) THE FRONTEND (What the user sees)
│   ├── index.html              <-- Main public website (Home, Login, Register, Packages)
│   ├── admin.html              <-- Admin dashboard (Manage bookings, packages)
│   ├── css/                    <-- (Directory) Stylesheets for making the site look good
│   ├── js/                     <-- (Directory) JavaScript files for frontend logic and API calls
│   └── images/                 <-- (Directory) Pictures used on the website
│
└── src/                        <-- (Directory) THE BACKEND (The Java Server)
    └── main/
        └── java/
            └── com/
                └── travelgo/   <-- Main Java Package
                    │
                    ├── server/
                    │   └── TravelGoServer.java    <-- The Entry Point (has the main() method)
                    │
                    ├── models/                    <-- The Data Blueprints
                    │   ├── User.java
                    │   ├── TravelPackage.java
                    │   └── Booking.java
                    │
                    ├── dao/                       <-- The "Database" Layer
                    │   └── DataStore.java         <-- Holds data in computer memory (RAM)
                    │
                    ├── handlers/                  <-- The API Controllers
                    │   ├── AuthHandler.java       <-- Handles /api/auth (Login/Register)
                    │   ├── PackageHandler.java    <-- Handles /api/packages
                    │   ├── BookingHandler.java    <-- Handles /api/bookings
                    │   └── StaticFileHandler.java <-- Reads files from the 'webapp' folder and sends them to the browser
                    │
                    └── service/
                        └── EmailService.java      <-- Handles sending confirmation emails



compilation code : ./run.sh
