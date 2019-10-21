package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.model.Member;
import com.bmw.remotecollab.admin.service.email.AwsSenderService;
import com.bmw.remotecollab.admin.service.email.Email;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;

import java.util.ArrayList;
import java.util.List;



@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;
    @MockBean
    private AwsSenderService awsSenderService;
    @Autowired
    TemplateEngine templateEngine;
    @Captor
    private ArgumentCaptor<Email> email;

    @Value("${email.service.link.url}") String linkUrl;

    private static final String testEmail = "test@email.com";

    @Test
    @Ignore
    public void testSendInvitationEmail() {
        List<Member> members = new ArrayList<>();
        members.add(new Member(testEmail));
        emailService.sendInvitationEmail("123123", members);
        Mockito.verify(awsSenderService, Mockito.times(1)).sendEmail(email.capture());
        Email inputEmail = email.getValue();
        Assert.assertEquals(inputEmail.getTo().get(0), testEmail );
        Assert.assertTrue(inputEmail.isHtml());
        Assert.assertEquals(inputEmail.getSubject(), "Attend online meeting");
        Assert.assertTrue(inputEmail.getBody().contains(this.linkUrl));
    }

}
