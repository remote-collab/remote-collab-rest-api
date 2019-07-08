package com.bmw.remotecollab.admin.rest;

import io.openvidu.java.client.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api-sessions")
public class SessionController {
    OpenVidu openVidu;

    private Map<Long, Session> lessonIdSession = new ConcurrentHashMap<>();
    private String lastSession = "";
    private Map<String, Session> sessionIdUserIdToken = new ConcurrentHashMap<>();

    private String OPENVIDU_URL;
    private String SECRET;

    public SessionController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
    }

    @RequestMapping(value = "/create-session", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> createSession(@RequestBody String lessonId) {
        try {
            Session session = this.openVidu.createSession();

            this.sessionIdUserIdToken.put(session.getSessionId(), session);

            System.out.println("Session created ...");
            showMap();
            JSONObject responseJson = new JSONObject();
            responseJson.put("sessionId", session.getSessionId());

            return new ResponseEntity<>(responseJson, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponse(e);
        }

    }

    static int counter = 1;

    @RequestMapping(value = "/generate-token", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> generateToken(@RequestBody String sessionId) {

        sessionId = sessionId.replace("%22=", "");

        System.out.println("Creating token for '" + sessionId + "'");
        Session session = this.sessionIdUserIdToken.get(sessionId);
        OpenViduRole role =  this.lastSession.endsWith(sessionId) ? OpenViduRole.SUBSCRIBER : OpenViduRole.PUBLISHER; //  OpenViduRole.SUBSCRIBER; //
        JSONObject responseJson = new JSONObject();

        TokenOptions tokenOpts = new TokenOptions.Builder().role(role)
                .data("SERVER=" + "TODO-user" + counter++).build();
        this.lastSession = sessionId;
        try {
            String token = session.generateToken(tokenOpts);

            responseJson.put("token", token);

            showMap();

            return new ResponseEntity<>(responseJson, HttpStatus.OK);
        } catch (OpenViduJavaClientException e1) {
            // If internal error generate an error message and return it to client
            return getErrorResponse(e1);
        } catch (OpenViduHttpException e2) {
            if (404 == e2.getStatus()) {
                // Invalid sessionId (user left unexpectedly). Session object is not valid
                // anymore. Must clean invalid session and create a new one
                try {
                    this.sessionIdUserIdToken.remove(session.getSessionId());
                    session = this.openVidu.createSession();
                    this.sessionIdUserIdToken.put(session.getSessionId(), session);
                    String token = session.generateToken(tokenOpts);
                    // END IMPORTANT STUFF

                    responseJson.put("token", token);
                    showMap();

                    return new ResponseEntity<>(responseJson, HttpStatus.OK);
                } catch (OpenViduJavaClientException | OpenViduHttpException e3) {
                    return getErrorResponse(e3);
                }
            } else {
                return getErrorResponse(e2);
            }
        }
    }

    private ResponseEntity<JSONObject> getErrorResponse(Exception e) {
        JSONObject json = new JSONObject();
        json.put("cause", e.getCause());
        json.put("error", e.getMessage());
        json.put("exception", e.getClass());
        return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void showMap() {
        System.out.println("------------------------------");
        System.out.println(this.lessonIdSession.toString());
        System.out.println(this.sessionIdUserIdToken.toString());
        System.out.println("------------------------------");
    }

}
