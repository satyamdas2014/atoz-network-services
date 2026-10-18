package com.atoz.atoz_network_services.ServiceImpl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.atoz.atoz_network_services.Entity.Complaint;
import com.atoz.atoz_network_services.Entity.CustomerEnquiry;
import com.atoz.atoz_network_services.Entity.ServiceRequest;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${owner.email}")
    private String ownerEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCustomerEnquiryEmail(CustomerEnquiry enquiry) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(ownerEmail);

        message.setSubject(
                "New Customer Enquiry - A To Z Network Services");

        String emailBody =
                "New Customer Enquiry Received\n\n"
                + "Customer Details\n"
                + "-------------------------\n"
                + "Name: " + enquiry.getName() + "\n"
                + "Mobile: " + enquiry.getMobile() + "\n"
                + "Email: " + enquiry.getEmail() + "\n"
                + "Address: " + enquiry.getAddress() + "\n"
                + "Connection Type: " + enquiry.getConnectionType() + "\n"
                + "Plan: " + enquiry.getPlan() + "\n"
                + "Message: " + enquiry.getMessage() + "\n"
                + "Submitted At: " + enquiry.getCreatedAt();

        message.setText(emailBody);

        mailSender.send(message);
    }

public void sendComplaintEmail(Complaint complaint) {

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(ownerEmail);
    message.setSubject(
            "New Complaint - A To Z Network Services");

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

    message.setText(body);
    mailSender.send(message);
}

public void sendServiceRequestEmail(ServiceRequest request) {

    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo(ownerEmail);
    message.setSubject(
            "New Service Request - A To Z Network Services");

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

    message.setText(body);

    mailSender.send(message);
}


public void sendEnquiryConfirmationToCustomer(CustomerEnquiry enquiry) {

    if (enquiry.getEmail() == null
            || enquiry.getEmail().isBlank()) {
        return;
    }

    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo(enquiry.getEmail());
    message.setSubject(
            "Enquiry Received - A To Z Network Services");

    String emailBody =
            "Dear " + enquiry.getName() + ",\n\n"
            + "Thank you for contacting A To Z Network Services.\n\n"
            + "We have received your enquiry successfully.\n"
            + "Our team will contact you shortly.\n\n"
            + "Your Enquiry Details:\n"
            + "Enquiry ID: " + enquiry.getId() + "\n"
            + "Connection Type: " + enquiry.getConnectionType() + "\n"
            + "Plan: " + enquiry.getPlan() + "\n\n"
            + "Regards,\n"
            + "A To Z Network Services";

    message.setText(emailBody);

    mailSender.send(message);
}

}