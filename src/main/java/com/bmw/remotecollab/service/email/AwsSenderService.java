package com.bmw.remotecollab.service.email;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AwsSenderService {

    @Value("rc.email.invite.enabled")
    private boolean sendInvites;

    private static final Logger logger = LoggerFactory.getLogger(AwsSenderService.class);

    private AmazonSimpleEmailService sesClient;

    private boolean initialized = false;

    public AwsSenderService(@Value("${amazon.aws.region}") String awsRegion) {
        try {
            AWSCredentialsProvider awsCreds = new EnvironmentVariableCredentialsProvider();

            sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(awsCreds)
                    .withRegion(awsRegion)
                    .build();
            this.initialized = true;
        } catch (Exception ex) {
            logger.warn("Problem calling AWS SES.", ex);
        }

        logger.info("Amazon SES is up and running.");
    }

    public void sendEmail(Email email){
        if (sendInvites) {
            if (initialized) {
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
                } catch (SdkClientException aex) {
                    logger.warn("Email could not be send. {}", aex.getMessage());
                }
            } else {
                logger.info("No email will be sent since the client could not be initialized.");
            }
        } else {
            logger.info("Not sending email due to configuration 'rc.email.invite.enabled'");
        }
    }

}
