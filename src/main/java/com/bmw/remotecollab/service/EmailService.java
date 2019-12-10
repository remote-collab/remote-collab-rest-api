package com.bmw.remotecollab.service;

import com.bmw.remotecollab.model.Member;
import com.bmw.remotecollab.model.Room;
import com.bmw.remotecollab.service.email.AwsSenderService;
import com.bmw.remotecollab.service.email.Email;
import com.bmw.remotecollab.service.email.From;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String INVITE_TEMPLATE = "InviteEmailTemplate";

    private final String fromEmail;
    private final String fromEmailName;
    private final String linkUrl;
    private AwsSenderService awsSenderService;
    private TemplateEngine templateEngine;

    public EmailService(@Value("${email.service.from}") String fromEmail, @Value("${email.service.from.name}") String fromEmailName, @Value("${email.service.link.url}") String linkUrl,
                        @Autowired AwsSenderService awsSenderService, @Autowired TemplateEngine templateEngine) {
        this.fromEmail = fromEmail;
        this.fromEmailName = fromEmailName;
        this.linkUrl = linkUrl;
        this.awsSenderService = awsSenderService;
        this.templateEngine = templateEngine;
    }

    void sendInvitationEmail(final Room room) {
        final List<Member> members = room.getMembers();

        if(!members.isEmpty()) {
            logger.info("Sending invitation emails for Room: {} - Members: {}", room.getId(), room.getMembers());

            String subject = "Attend online meeting";
            String body = buildBody(room, INVITE_TEMPLATE);

            Email.EmailBuilder emailBuilder = Email.builder();
            emailBuilder.from(new From(this.fromEmail, this.fromEmailName));
            emailBuilder.to(members.stream().map(Member::getEmail).collect(Collectors.toList()));
            emailBuilder.subject(subject);
            emailBuilder.body(body);
            emailBuilder.html(true);

            awsSenderService.sendEmail(emailBuilder.build());
        } else {
            logger.info("Nothing to send. No emails set for room.");
        }
    }

    private String buildBody(Room room, @SuppressWarnings("SameParameterValue") String mailTemplate) {
        Context context = new Context();
        String url = this.linkUrl + room.getId();
        context.setVariable("roomName", room.getName());
        context.setVariable("roomUUID", room.getId());
        context.setVariable("url", url);
        return templateEngine.process(mailTemplate, context);
    }

}
