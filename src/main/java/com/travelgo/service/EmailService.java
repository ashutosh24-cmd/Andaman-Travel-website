package com.travelgo.service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.Properties;

/**
 * Email Service - Handles sending booking confirmation and cancellation emails.
 * Demonstrates: Encapsulation, Single Responsibility Principle.
 */
public class EmailService {
    private static EmailService instance;
    private Properties emailProps;
    private String smtpUser;
    private String smtpPass;
    private String adminEmail;
    private String companyName;
    private String companyPhone;
    private String companyWebsite;
    private boolean configured = false;

    private EmailService() {
        loadConfig();
    }

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }

    private void loadConfig() {
        emailProps = new Properties();
        try (FileInputStream fis = new FileInputStream("email.properties")) {
            Properties config = new Properties();
            config.load(fis);

            smtpUser = config.getProperty("smtp.username", "");
            smtpPass = config.getProperty("smtp.password", "");
            adminEmail = config.getProperty("admin.email", "ashutoshvarma393@gmail.com");
            companyName = config.getProperty("company.name", "Unfold Andaman");
            companyPhone = config.getProperty("company.phone", "+91 98765 43210");
            companyWebsite = config.getProperty("company.website", "http://localhost:8080");

            emailProps.put("mail.smtp.auth", config.getProperty("smtp.auth", "true"));
            emailProps.put("mail.smtp.starttls.enable", config.getProperty("smtp.starttls", "true"));
            emailProps.put("mail.smtp.host", config.getProperty("smtp.host", "smtp.gmail.com"));
            emailProps.put("mail.smtp.port", config.getProperty("smtp.port", "587"));

            configured = !smtpPass.equals("YOUR_APP_PASSWORD_HERE") && !smtpPass.isEmpty();
            if (configured) {
                System.out.println("[Email] Service configured successfully. Admin: " + adminEmail);
            } else {
                System.out.println("[Email] WARNING: App password not set in email.properties. Emails will be logged only.");
            }
        } catch (IOException e) {
            System.out.println("[Email] email.properties not found. Emails will be logged only.");
        }
    }

    private Session getSession() {
        return Session.getInstance(emailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPass);
            }
        });
    }

    private boolean sendEmail(String to, String subject, String htmlBody) {
        System.out.println("\n[Email] ─────────────────────────────────────");
        System.out.println("[Email] To: " + to);
        System.out.println("[Email] Subject: " + subject);

        if (!configured) {
            System.out.println("[Email] (Simulated - configure email.properties to send real emails)");
            System.out.println("[Email] ─────────────────────────────────────\n");
            return true;
        }

        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(smtpUser, companyName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=UTF-8");
            Transport.send(message);
            System.out.println("[Email] Sent successfully!");
            System.out.println("[Email] ─────────────────────────────────────\n");
            return true;
        } catch (Exception e) {
            System.out.println("[Email] FAILED: " + e.getMessage());
            System.out.println("[Email] ─────────────────────────────────────\n");
            return false;
        }
    }

    // ── Booking Confirmation to Customer ──
    public boolean sendBookingConfirmation(String customerName, String customerEmail,
            String bookingId, String packageName, String destination, String travelDate,
            int numTravelers, double totalAmount, String hotelName, String cabType,
            String ticketDetails, String localTourInfo, int durationDays) {

        String subject = "Booking Confirmed! Your " + packageName + " Trip - " + companyName;
        String html = getBookingConfirmationTemplate(customerName, bookingId, packageName,
                destination, travelDate, numTravelers, totalAmount, hotelName, cabType,
                ticketDetails, localTourInfo, durationDays);

        return sendEmail(customerEmail, subject, html);
    }

    // ── Admin Notification for New Booking ──
    public boolean sendAdminBookingNotification(String customerName, String customerEmail,
            String customerPhone, String bookingId, String packageName, String travelDate,
            int numTravelers, double totalAmount) {

        String subject = "New Booking #" + bookingId + " - " + customerName + " | " + companyName;
        String html = getAdminNotificationTemplate(customerName, customerEmail, customerPhone,
                bookingId, packageName, travelDate, numTravelers, totalAmount);

        return sendEmail(adminEmail, subject, html);
    }

    // ── Cancellation Confirmation to Customer ──
    public boolean sendCancellationConfirmation(String customerName, String customerEmail,
            String bookingId, String packageName, double refundAmount) {

        String subject = "Booking Cancelled - #" + bookingId + " | " + companyName;
        String html = getCancellationTemplate(customerName, bookingId, packageName, refundAmount);

        return sendEmail(customerEmail, subject, html);
    }

    // ── Admin Notification for Cancellation ──
    public boolean sendAdminCancellationNotification(String customerName, String customerEmail,
            String bookingId, String packageName, double refundAmount) {

        String subject = "Booking Cancelled #" + bookingId + " - " + customerName + " | " + companyName;
        String html = getAdminCancellationTemplate(customerName, customerEmail, bookingId,
                packageName, refundAmount);

        return sendEmail(adminEmail, subject, html);
    }

    // ══════════════════════════════════════════
    //  HTML EMAIL TEMPLATES
    // ══════════════════════════════════════════

    private String getBookingConfirmationTemplate(String name, String bookingId, String pkgName,
            String destination, String travelDate, int travelers, double total,
            String hotel, String cab, String tickets, String tours, int days) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;font-family:Arial,sans-serif;background:#f4f7fa;'>"
            + "<div style='max-width:600px;margin:0 auto;background:#ffffff;'>"
            // Header
            + "<div style='background:linear-gradient(135deg,#0c4a6e,#0ea5e9);padding:30px;text-align:center;'>"
            + "<h1 style='color:#fff;margin:0;font-size:24px;'>Unfold Andaman</h1>"
            + "<p style='color:rgba(255,255,255,0.8);margin:5px 0 0;font-size:14px;'>Your Island Adventure Awaits</p>"
            + "</div>"
            // Success Banner
            + "<div style='background:#10b981;padding:20px;text-align:center;'>"
            + "<h2 style='color:#fff;margin:0;font-size:20px;'>&#10004; Booking Confirmed!</h2>"
            + "</div>"
            // Body
            + "<div style='padding:30px;'>"
            + "<p style='font-size:16px;color:#1e293b;'>Dear <strong>" + name + "</strong>,</p>"
            + "<p style='color:#64748b;line-height:1.6;'>Thank you for choosing Unfold Andaman! Your booking has been confirmed. Here are your trip details:</p>"
            // Booking ID Card
            + "<div style='background:#f8fafc;border-radius:12px;padding:20px;margin:20px 0;border-left:4px solid #0ea5e9;'>"
            + "<p style='margin:0 0 5px;color:#64748b;font-size:13px;'>BOOKING REFERENCE</p>"
            + "<p style='margin:0;font-size:24px;font-weight:bold;color:#0c4a6e;'>#" + bookingId + "</p>"
            + "</div>"
            // Trip Details
            + "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>"
            + "<tr><td style='padding:12px;border-bottom:1px solid #e2e8f0;color:#64748b;width:40%;'>Package</td>"
            + "<td style='padding:12px;border-bottom:1px solid #e2e8f0;font-weight:600;color:#1e293b;'>" + pkgName + "</td></tr>"
            + "<tr><td style='padding:12px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Destination</td>"
            + "<td style='padding:12px;border-bottom:1px solid #e2e8f0;font-weight:600;color:#1e293b;'>" + destination + "</td></tr>"
            + "<tr><td style='padding:12px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Travel Date</td>"
            + "<td style='padding:12px;border-bottom:1px solid #e2e8f0;font-weight:600;color:#1e293b;'>" + travelDate + "</td></tr>"
            + "<tr><td style='padding:12px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Duration</td>"
            + "<td style='padding:12px;border-bottom:1px solid #e2e8f0;font-weight:600;color:#1e293b;'>" + days + " Days / " + (days-1) + " Nights</td></tr>"
            + "<tr><td style='padding:12px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Travelers</td>"
            + "<td style='padding:12px;border-bottom:1px solid #e2e8f0;font-weight:600;color:#1e293b;'>" + travelers + " Person(s)</td></tr>"
            + "</table>"
            // Inclusions
            + "<h3 style='color:#0c4a6e;margin:25px 0 15px;'>Package Inclusions</h3>"
            + "<div style='background:#f8fafc;border-radius:12px;padding:15px;'>"
            + "<p style='margin:8px 0;color:#1e293b;'>&#127976; <strong>Hotel:</strong> " + hotel + "</p>"
            + "<p style='margin:8px 0;color:#1e293b;'>&#128663; <strong>Cab:</strong> " + cab + "</p>"
            + "<p style='margin:8px 0;color:#1e293b;'>&#127915; <strong>Tickets:</strong> " + tickets + "</p>"
            + "<p style='margin:8px 0;color:#1e293b;'>&#127961; <strong>Local Tours:</strong> " + tours + "</p>"
            + "</div>"
            // Total
            + "<div style='background:#0c4a6e;border-radius:12px;padding:20px;margin:25px 0;text-align:center;'>"
            + "<p style='color:rgba(255,255,255,0.7);margin:0 0 5px;font-size:13px;'>TOTAL AMOUNT</p>"
            + "<p style='color:#fff;margin:0;font-size:28px;font-weight:bold;'>&#8377;" + String.format("%,.0f", total) + "</p>"
            + "</div>"
            // Note
            + "<div style='background:#fef3c7;border-radius:8px;padding:15px;margin:15px 0;'>"
            + "<p style='margin:0;color:#92400e;font-size:13px;'><strong>Important:</strong> Please carry a valid photo ID during travel. For any changes, contact us at least 48 hours before departure.</p>"
            + "</div>"
            + "</div>"
            // Footer
            + "<div style='background:#f8fafc;padding:20px;text-align:center;border-top:1px solid #e2e8f0;'>"
            + "<p style='margin:0;color:#64748b;font-size:13px;'>Need help? Contact us</p>"
            + "<p style='margin:5px 0;color:#0c4a6e;font-weight:600;'>" + companyPhone + " | info@unfoldandaman.com</p>"
            + "<p style='margin:10px 0 0;color:#94a3b8;font-size:11px;'>&copy; 2025 Unfold Andaman. All rights reserved.</p>"
            + "</div></div></body></html>";
    }

    private String getAdminNotificationTemplate(String name, String email, String phone,
            String bookingId, String pkgName, String travelDate, int travelers, double total) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;font-family:Arial,sans-serif;background:#f4f7fa;'>"
            + "<div style='max-width:600px;margin:0 auto;background:#ffffff;'>"
            + "<div style='background:#0c4a6e;padding:25px;text-align:center;'>"
            + "<h1 style='color:#fff;margin:0;font-size:20px;'>&#128276; New Booking Alert</h1>"
            + "</div>"
            + "<div style='padding:25px;'>"
            + "<div style='background:#ecfdf5;border-radius:10px;padding:15px;margin-bottom:20px;text-align:center;'>"
            + "<p style='color:#059669;font-size:18px;font-weight:bold;margin:0;'>New Booking Received!</p>"
            + "<p style='color:#059669;font-size:13px;margin:5px 0 0;'>Booking ID: #" + bookingId + "</p>"
            + "</div>"
            + "<h3 style='color:#0c4a6e;margin:0 0 15px;'>Customer Details</h3>"
            + "<table style='width:100%;border-collapse:collapse;'>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;width:35%;'>Name</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;font-weight:600;'>" + name + "</td></tr>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Email</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;'><a href='mailto:" + email + "'>" + email + "</a></td></tr>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Phone</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;font-weight:600;'>" + phone + "</td></tr>"
            + "</table>"
            + "<h3 style='color:#0c4a6e;margin:20px 0 15px;'>Booking Details</h3>"
            + "<table style='width:100%;border-collapse:collapse;'>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;width:35%;'>Package</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;font-weight:600;'>" + pkgName + "</td></tr>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Travel Date</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;'>" + travelDate + "</td></tr>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Travelers</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;'>" + travelers + "</td></tr>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Amount</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;font-weight:bold;color:#f97316;font-size:18px;'>&#8377;" + String.format("%,.0f", total) + "</td></tr>"
            + "</table>"
            + "<div style='margin-top:25px;text-align:center;'>"
            + "<a href='" + companyWebsite + "/admin.html' style='background:#0c4a6e;color:#fff;padding:12px 30px;border-radius:8px;text-decoration:none;font-weight:600;display:inline-block;'>View in Admin Panel</a>"
            + "</div></div>"
            + "<div style='background:#f8fafc;padding:15px;text-align:center;border-top:1px solid #e2e8f0;'>"
            + "<p style='margin:0;color:#94a3b8;font-size:11px;'>This is an automated notification from Unfold Andaman Booking System</p>"
            + "</div></div></body></html>";
    }

    private String getCancellationTemplate(String name, String bookingId, String pkgName, double refund) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;font-family:Arial,sans-serif;background:#f4f7fa;'>"
            + "<div style='max-width:600px;margin:0 auto;background:#ffffff;'>"
            + "<div style='background:linear-gradient(135deg,#0c4a6e,#0ea5e9);padding:30px;text-align:center;'>"
            + "<h1 style='color:#fff;margin:0;font-size:24px;'>Unfold Andaman</h1>"
            + "</div>"
            + "<div style='background:#ef4444;padding:20px;text-align:center;'>"
            + "<h2 style='color:#fff;margin:0;font-size:20px;'>Booking Cancelled</h2>"
            + "</div>"
            + "<div style='padding:30px;'>"
            + "<p style='font-size:16px;color:#1e293b;'>Dear <strong>" + name + "</strong>,</p>"
            + "<p style='color:#64748b;line-height:1.6;'>Your booking has been cancelled as per your request. Below are the details:</p>"
            + "<div style='background:#fef2f2;border-radius:12px;padding:20px;margin:20px 0;border-left:4px solid #ef4444;'>"
            + "<p style='margin:0 0 5px;color:#64748b;font-size:13px;'>CANCELLED BOOKING</p>"
            + "<p style='margin:0;font-size:22px;font-weight:bold;color:#dc2626;'>#" + bookingId + "</p>"
            + "<p style='margin:10px 0 0;color:#1e293b;'>Package: <strong>" + pkgName + "</strong></p>"
            + "</div>"
            + "<div style='background:#f0fdf4;border-radius:12px;padding:20px;margin:20px 0;text-align:center;'>"
            + "<p style='color:#059669;margin:0 0 5px;font-size:13px;'>REFUND AMOUNT</p>"
            + "<p style='color:#059669;margin:0;font-size:28px;font-weight:bold;'>&#8377;" + String.format("%,.0f", refund) + "</p>"
            + "<p style='color:#64748b;margin:10px 0 0;font-size:12px;'>Refund will be processed within 5-7 business days</p>"
            + "</div>"
            + "<p style='color:#64748b;font-size:13px;'>If you have any questions about the cancellation or refund, please don't hesitate to reach out.</p>"
            + "</div>"
            + "<div style='background:#f8fafc;padding:20px;text-align:center;border-top:1px solid #e2e8f0;'>"
            + "<p style='margin:0;color:#64748b;font-size:13px;'>Need help? Contact us</p>"
            + "<p style='margin:5px 0;color:#0c4a6e;font-weight:600;'>" + companyPhone + " | info@unfoldandaman.com</p>"
            + "<p style='margin:10px 0 0;color:#94a3b8;font-size:11px;'>&copy; 2025 Unfold Andaman. All rights reserved.</p>"
            + "</div></div></body></html>";
    }

    private String getAdminCancellationTemplate(String name, String email, String bookingId,
            String pkgName, double refund) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;font-family:Arial,sans-serif;background:#f4f7fa;'>"
            + "<div style='max-width:600px;margin:0 auto;background:#ffffff;'>"
            + "<div style='background:#dc2626;padding:25px;text-align:center;'>"
            + "<h1 style='color:#fff;margin:0;font-size:20px;'>&#10060; Booking Cancellation Alert</h1>"
            + "</div>"
            + "<div style='padding:25px;'>"
            + "<table style='width:100%;border-collapse:collapse;'>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;width:35%;'>Booking ID</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;font-weight:bold;color:#dc2626;'>#" + bookingId + "</td></tr>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Customer</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;font-weight:600;'>" + name + " (" + email + ")</td></tr>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Package</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;'>" + pkgName + "</td></tr>"
            + "<tr><td style='padding:10px;border-bottom:1px solid #e2e8f0;color:#64748b;'>Refund Due</td>"
            + "<td style='padding:10px;border-bottom:1px solid #e2e8f0;font-weight:bold;color:#f97316;font-size:18px;'>&#8377;" + String.format("%,.0f", refund) + "</td></tr>"
            + "</table>"
            + "<div style='margin-top:25px;text-align:center;'>"
            + "<a href='" + companyWebsite + "/admin.html' style='background:#0c4a6e;color:#fff;padding:12px 30px;border-radius:8px;text-decoration:none;font-weight:600;display:inline-block;'>View in Admin Panel</a>"
            + "</div></div>"
            + "<div style='background:#f8fafc;padding:15px;text-align:center;border-top:1px solid #e2e8f0;'>"
            + "<p style='margin:0;color:#94a3b8;font-size:11px;'>Automated notification from Unfold Andaman</p>"
            + "</div></div></body></html>";
    }
}
