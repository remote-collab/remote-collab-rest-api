package com.bmw.remotecollab.admin.rest;

import com.bmw.remotecollab.admin.model.Room;
import com.bmw.remotecollab.admin.service.OpenViduService;
import com.bmw.remotecollab.admin.service.RoomService;
import io.openvidu.java.client.*;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    private Map<Long, Session> lessonIdSession = new ConcurrentHashMap<>();
    private Map<String, Session> sessionIdUserIdToken = new ConcurrentHashMap<>();

    @Autowired
    private OpenViduService openViduService;
    @Autowired
    private RoomService roomService;


    public SessionController() {
    }

    @RequestMapping(value = "/create-session", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> createSession(@RequestBody CreateSessionRequest requestBody) {
        try {

            Session session = openViduService.createSession(requestBody.customSessionId);

            this.sessionIdUserIdToken.put(session.getSessionId(), session);

            logger.info("Session created. ID=" + session.getSessionId() + ", CustomSessionId=" + requestBody.getCustomSessionId());
            showMap();
            JSONObject responseJson = new JSONObject();
            responseJson.put("sessionId", session.getSessionId());

            return new ResponseEntity<>(responseJson, HttpStatus.OK);
        } catch (Exception e) {
            return getErrorResponse(e);
        }
    }

    @RequestMapping(value = "/generate-token", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> generateToken(@RequestBody String sessionId) {

        sessionId = sessionId.replace("%22=", "");

        logger.info("Creating token for '" + sessionId + "'");
        try {
            String token = this.openViduService.getTokenForSession(sessionId);


            JSONObject responseJson = new JSONObject();
            responseJson.put("token", token);

            showMap();

            return new ResponseEntity<>(responseJson, HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            // If internal error generate an error message and return it to client
            return getErrorResponse(e);
        }
    }

    ////////////// NEW API /////////////////////////////

    /**
     * Create a new room to start a shared video session.
     *
     * @param requestNewRoom request contains the name that will be displayed during the session.
     *
     * @return UUID to generate the link for all participants.
     */
    @RequestMapping(value = "/create-room", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> createNewRoom(@RequestBody RequestNewRoom requestNewRoom){
        String roomName = requestNewRoom.getRoomName();
        String id = roomService.createNewRoom(roomName);
        logger.info("Created new room '{}' with UUID={}", roomName, id);
        JSONObject responseJson = new JSONObject();
        responseJson.put("uuid", id);
        return new ResponseEntity<>(responseJson, HttpStatus.OK);
    }

    /**
     * Join an existing room. Returns an error in case the room does not exists.
     *
     * @param requestJoinRoom  request contains the uuid of the room.
     *
     * @return response contains a token to start an openvidu session.
     */
    @RequestMapping(value = "/join-room", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> joinRoom(@RequestBody RequestJoinRoom requestJoinRoom){
        String roomUUID = requestJoinRoom.getRoomUUID();
        boolean exists = roomService.doesRoomExists(roomUUID);
        if(exists){
            Room room = roomService.findById(roomUUID);
            logger.debug("Found {}", room);
            Session session = openViduService.createSession(room.getName());
            try {
                logger.debug("Created session with id: {}", session.getSessionId());
                String token = openViduService.getTokenForSession(session);
                JSONObject responseJson = new JSONObject();
                responseJson.put("token", token);
                responseJson.put("roomName", room.getName());
                return new ResponseEntity<>(responseJson, HttpStatus.OK);
            } catch (OpenViduJavaClientException | OpenViduHttpException e) {
                logger.warn("Problem calling openvidu server.", e);
                return getErrorResponse(e);
            }

        } else {
            JSONObject responseJson = new JSONObject();
            responseJson.put("problem", "Room does not exists.");
            return new ResponseEntity<>(responseJson, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    ////////////// internal /////////////////////////////

    private ResponseEntity<JSONObject> getErrorResponse(Exception e) {
        JSONObject json = new JSONObject();
        json.put("cause", e.getCause());
        json.put("error", e.getMessage());
        json.put("exception", e.getClass());
        return new ResponseEntity<>(json, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void showMap() {
        logger.debug("------------------------------");
        logger.debug(this.lessonIdSession.toString());
        logger.debug(this.sessionIdUserIdToken.toString());
        logger.debug("------------------------------");
    }

}
