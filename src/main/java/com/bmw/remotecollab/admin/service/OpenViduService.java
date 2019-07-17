package com.bmw.remotecollab.admin.service;

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

    private Map<String, Session> sessionStorageCache = new ConcurrentHashMap<>();

    public OpenViduService(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        this.openVidu = new OpenVidu(openviduUrl, secret);
    }

    public String startRecording( String sessionId ) throws OpenViduJavaClientException, OpenViduHttpException {
        RecordingProperties properties = new RecordingProperties.Builder()
                .outputMode(Recording.OutputMode.COMPOSED)
                .name("MY_RECORDING_NAME")
                .build();
        Recording recording = openVidu.startRecording(sessionId, properties); // Starts recording
        logger.info("Start recording: {}", recording);
        return recording.getId();
    }

    public void stopRecording(String recordingId) throws OpenViduJavaClientException, OpenViduHttpException {
        Recording recording = openVidu.stopRecording(recordingId); // Stops recording
        logger.info("Stopped recording: {}", recording);
    }

    public Session createSession(String roomName) {
        SessionProperties.Builder customProperties = new SessionProperties.Builder();
        customProperties.customSessionId(roomName);
        customProperties.recordingMode(RecordingMode.MANUAL);
        customProperties.defaultOutputMode(Recording.OutputMode.INDIVIDUAL);
        Session session = null;
        try {
            session = this.openVidu.createSession(customProperties.build());
            this.sessionStorageCache.put(session.getSessionId(), session);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            logger.warn("Problem calling openvidu server.", e);
        }
        return session;
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
                Session removedSession = this.sessionStorageCache.remove(session.getSessionId());
                session = this.openVidu.createSession(removedSession.getProperties());
                this.sessionStorageCache.put(session.getSessionId(), session);

                token = session.generateToken(tokenOpts);
            }
        }
        return token;
    }
}
