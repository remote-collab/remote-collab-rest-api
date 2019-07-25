package com.bmw.remotecollab.admin.service.email;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AwsSenderService {

    private static final Logger logger = LoggerFactory.getLogger(AwsSenderService.class);

    private AmazonSimpleEmailService sesClient;

    private boolean initialized = false;

    public AwsSenderService() {
        try {
            AWSCredentialsProvider awsCreds = new ClasspathPropertiesFileCredentialsProvider();

            sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(awsCreds)
                    .withRegion(Regions.EU_WEST_1)
                    .build();
            this.initialized = true;
        } catch (Exception ex) {
            logger.warn("Problem calling AWS SES.", ex);
        }

        logger.info("Amazon SES is up and running.");
    }

    public void sendEmail(Email email){
        if(initialized) {
            // Construct an object to contain the recipient address.
            Destination destination = new Destination().withToAddresses(email.getTo());
            //set cc & bcc addresses
            if (email.getCc() != null && email.getCc().size() > 0) {
                destination.withCcAddresses(email.getCc());
            }
            if (email.getBcc() != null && email.getBcc().size() > 0) {
                destination.withBccAddresses(email.getBcc());
            }
            // Create the subject and body of the message.
            Content subject = new Content().withData(email.getSubject());
            Content textBody = new Content().withData(email.getBody());
            Body body = email.isHtml() ? new Body().withHtml(textBody) : new Body().withText(textBody);

            // Create a message with the specified subject and body.
            Message message = new Message().withSubject(subject).withBody(body);
            // Assemble the email.
            SendEmailRequest request = new SendEmailRequest().withSource(email.getFrom())
                    .withReplyToAddresses(email.getFrom())
                    .withDestination(destination)
                    .withMessage(message);
            // Send the email.
            try {
                SendEmailResult result = sesClient.sendEmail(request);
                logger.info("Sent email via aws ses service. ID={}", result.getMessageId());
            } catch (SdkClientException aex){
                logger.warn("Email could not be send. {}", aex.getMessage());
            }
        }
    }

}
