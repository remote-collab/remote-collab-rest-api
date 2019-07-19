package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.model.Member;
import com.bmw.remotecollab.admin.service.email.AwsSenderService;
import com.bmw.remotecollab.admin.service.email.Email;
import com.bmw.remotecollab.admin.service.email.From;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    AwsSenderService awsSenderService;

    public void sendInvitationEmail(String roomId, List<Member> members){
        logger.info("Email sending not implemented yet. Room: {} - Members: {}", roomId, members);
        String body = "Testbody";
        String subject = "Testsubject";
        Email.EmailBuilder emailBuilder = Email.builder();
        emailBuilder.from(From.NO_REPLY);
        emailBuilder.to(members.stream().map(Member::getEmail).collect(Collectors.toList()));
        emailBuilder.subject(subject);
        emailBuilder.body(body);
        emailBuilder.html(false);

        awsSenderService.sendEmail(emailBuilder.build());
    }

}
