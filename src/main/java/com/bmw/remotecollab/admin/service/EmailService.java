package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.model.Member;
import com.bmw.remotecollab.admin.service.email.AwsSenderService;
import com.bmw.remotecollab.admin.service.email.Email;
import com.bmw.remotecollab.admin.service.email.From;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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

    void sendInvitationEmail(String roomId, Set<Member> members) {
        logger.info("Sending invitation emails for Room: {} - Members: {}", roomId, members);

        String subject = "Attend online meeting";
        String body = buildBody(INVITE_TEMPLATE, roomId);

        Email.EmailBuilder emailBuilder = Email.builder();
        emailBuilder.from(new From(this.fromEmail, this.fromEmailName));
        emailBuilder.to(members.stream().map(Member::getEmail).collect(Collectors.toList()));
        emailBuilder.subject(subject);
        emailBuilder.body(body);
        emailBuilder.html(true);

        awsSenderService.sendEmail(emailBuilder.build());
    }

    private String buildBody(String roomId, String mailTemplate) {
        Context context = new Context();
        String url = this.linkUrl + roomId;
        context.setVariable("url", url);
        context.setVariable("roomUUID", roomId);
        return templateEngine.process(mailTemplate, context);
    }

}
