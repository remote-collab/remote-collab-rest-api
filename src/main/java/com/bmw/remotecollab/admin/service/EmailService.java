package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.model.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendInvitationEmail(String roomId, List<Member> members){
        logger.info("Email sending not implemented yet. Room: {} - Members: {}", roomId, members);
    }
}
