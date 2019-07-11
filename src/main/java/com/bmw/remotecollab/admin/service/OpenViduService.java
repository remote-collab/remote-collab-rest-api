package com.bmw.remotecollab.admin.service;

import io.openvidu.java.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OpenViduService {

    private static final Logger logger = LoggerFactory.getLogger(OpenViduService.class);

    private OpenVidu openVidu;
    private String OPENVIDU_URL;
    private String SECRET;

    private Map<String, Session> sessionStorageCache = new ConcurrentHashMap<>();

    public OpenViduService(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
    }

    public Session createSession(String roomName) {
        SessionProperties.Builder customProperties = new SessionProperties.Builder();
        customProperties.customSessionId(roomName);
        Session session = null;
        try {
            session = this.openVidu.createSession(customProperties.build());
            this.sessionStorageCache.put(session.getSessionId(), session);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            logger.warn("Problem calling openvidu server.", e);
        }
        return session;
    }

    public String getTokenForSession(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = this.sessionStorageCache.get(sessionId);
        return getTokenForSession(session);
    }

    public String getTokenForSession(Session session) throws OpenViduJavaClientException, OpenViduHttpException {
        OpenViduRole role = OpenViduRole.PUBLISHER; //  OpenViduRole.SUBSCRIBER; //

        TokenOptions tokenOpts = new TokenOptions.Builder().role(role).build();

        String token = null;
        try {
            token = session.generateToken(tokenOpts);
        } catch (OpenViduJavaClientException e1) {
            logger.warn("Problem calling openvidu server.", e1);
            throw e1;
        } catch (OpenViduHttpException e2) {
            if (404 == e2.getStatus()) {
                // Invalid sessionId (user left unexpectedly). Session object is not valid
                // anymore. Must clean invalid session and create a new one
                try {
                    Session removedSession = this.sessionStorageCache.remove(session.getSessionId());
                    session = this.openVidu.createSession(removedSession.getProperties());
                    this.sessionStorageCache.put(session.getSessionId(), session);

                    token = session.generateToken(tokenOpts);

                } catch (OpenViduJavaClientException | OpenViduHttpException e3) {
                    throw e3;
                }
            }
        }
        return token;
    }
}
