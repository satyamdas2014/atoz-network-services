
package com.atoz.atoz_network_services.ServiceImpl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.atoz.atoz_network_services.Entity.Complaint;
import com.atoz.atoz_network_services.Entity.CustomerEnquiry;
import com.atoz.atoz_network_services.Entity.ServiceRequest;

@Service
public class EmailService {

    @Value("${owner.email}")
    private String ownerEmail;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public EmailService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private void sendEmail(
            String recipient,
            String subject,
            String body) {

        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient email is missing");
        }

        try {
            Map<String, Object> payload = Map.of(
                "sender", Map.of(
                    "name", "A To Z Network Services",
                    "email", senderEmail
                ),
                "to", List.of(
                    Map.of("email", recipient)
                ),
                "subject", subject,
                "textContent", body
            );

            String json = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                        "https://api.brevo.com/v3/smtp/email"))
                    .timeout(Duration.ofSeconds(20))
                    .header("accept", "application/json")
                    .header("api-key", apiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {
                throw new IllegalStateException(
                    "Brevo email API failed. HTTP status: "
                    + response.statusCode()
                    + ", response: " + response.body());
            }

            System.out.println(
                    "Email accepted by Brevo for: " + recipient);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Email request interrupted", e);

        } catch (Exception e) {
            System.err.println(
                    "Email sending failed: " + e.getMessage());
            throw new IllegalStateException(
                    "Unable to send email", e);
        }
    }

    public void sendCustomerEnquiryEmail(
            CustomerEnquiry enquiry) {

        String body =
                "New Customer Enquiry Received\n\n"
                + "Name: " + enquiry.getName() + "\n"
                + "Mobile: " + enquiry.getMobile() + "\n"
                + "Email: " + enquiry.getEmail() + "\n"
                + "Address: " + enquiry.getAddress() + "\n"
                + "Connection Type: " + enquiry.getConnectionType() + "\n"
                + "Plan: " + enquiry.getPlan() + "\n"
                + "Message: " + enquiry.getMessage() + "\n"
                + "Submitted At: " + enquiry.getCreatedAt();

        sendEmail(
                ownerEmail,
                "New Customer Enquiry - A To Z Network Services",
                body);
    }

    public void sendEnquiryConfirmationToCustomer(
            CustomerEnquiry enquiry) {

        if (enquiry.getEmail() == null
                || enquiry.getEmail().isBlank()) {
            return;
        }

        String body =
                "Dear " + enquiry.getName() + ",\n\n"
                + "Thank you for contacting A To Z Network Services.\n"
                + "We have received your enquiry successfully.\n"
                + "Our team will contact you shortly.\n\n"
                + "Enquiry ID: " + enquiry.getId() + "\n"
                + "Connection Type: " + enquiry.getConnectionType() + "\n"
                + "Plan: " + enquiry.getPlan() + "\n\n"
                + "Regards,\nA To Z Network Services";

        sendEmail(
                enquiry.getEmail(),
                "Enquiry Received - A To Z Network Services",
                body);
    }

    public void sendComplaintEmail(Complaint complaint) {

        String body =
                "New Complaint Received\n\n"
                + "Complaint ID: " + complaint.getId() + "\n"
                + "Name: " + complaint.getName() + "\n"
                + "Mobile: " + complaint.getMobile() + "\n"
                + "Subject: " + complaint.getSubject() + "\n"
                + "Address: " + complaint.getAddress() + "\n"
                + "Description: " + complaint.getDescription() + "\n"
                + "Priority: " + complaint.getPriority() + "\n"
                + "Status: " + complaint.getStatus();

        sendEmail(
                ownerEmail,
                "New Complaint - A To Z Network Services",
                body);
    }

    public void sendServiceRequestEmail(ServiceRequest request) {

        String body =
                "New Service Request Received\n\n"
                + "Request ID: " + request.getId() + "\n"
                + "Customer Name: " + request.getName() + "\n"
                + "Mobile: " + request.getMobile() + "\n"
                + "Request Type: " + request.getRequestType() + "\n"
                + "Address: " + request.getAddress() + "\n"
                + "Description: " + request.getDescription() + "\n"
                + "Status: " + request.getStatus() + "\n"
                + "Created At: " + request.getCreatedAt();

        sendEmail(
                ownerEmail,
                "New Service Request - A To Z Network Services",
                body);
    }
}
