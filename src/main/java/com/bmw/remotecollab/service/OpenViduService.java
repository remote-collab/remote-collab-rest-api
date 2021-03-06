package com.bmw.remotecollab.service;

import io.openvidu.java.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OpenViduService {

    private static final Logger logger = LoggerFactory.getLogger(OpenViduService.class);

    private final OpenVidu openVidu;

    public OpenViduService(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        this.openVidu = new OpenVidu(openviduUrl, secret);
    }

    public Session createSession(String roomName) {
        SessionProperties.Builder customProperties = new SessionProperties.Builder();
        customProperties.customSessionId(roomName);
        Session session = null;
        try {
            session = this.openVidu.createSession(customProperties.build());
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            logger.warn("Problem calling openvidu server.", e);
        }
        return session;
    }

    public Session getExistingSession(String sessionId){
        Session foundSession = this.openVidu.getActiveSessions().stream().filter(session -> session.getSessionId().equals(sessionId))
                .findFirst()
                .get();
        logger.info("Found session {}", foundSession.getSessionId());
        return foundSession;
    }

    public String getTokenForSession(Session session) throws OpenViduJavaClientException, OpenViduHttpException {

        TokenOptions tokenOpts = new TokenOptions.Builder().role(OpenViduRole.PUBLISHER).build();
        String token = null;
        try {
            token = session.generateToken(tokenOpts);
        } catch (OpenViduJavaClientException e1) {
            logger.warn("Problem calling openvidu server.", e1);
            throw e1;
        } catch (OpenViduHttpException e2) {
            if (HttpStatus.NOT_FOUND.value() == e2.getStatus()) {
                // Invalid sessionId (user left unexpectedly). Session object is not valid
                // anymore. Must clean invalid session and create a new one
                session = this.openVidu.createSession(session.getProperties());
                token = session.generateToken(tokenOpts);
            }
        }
        return token;
    }
}
