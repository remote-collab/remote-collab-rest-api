package com.bmw.remotecollab.admin.rest;

import com.bmw.remotecollab.admin.model.Room;
import com.bmw.remotecollab.admin.rest.exception.OpenViduException;
import com.bmw.remotecollab.admin.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.admin.rest.requests.RequestJoinRoom;
import com.bmw.remotecollab.admin.rest.requests.RequestNewRoom;
import com.bmw.remotecollab.admin.rest.response.ResponseJoinRoom;
import com.bmw.remotecollab.admin.rest.response.ResponseNewRoom;
import com.bmw.remotecollab.admin.service.OpenViduService;
import com.bmw.remotecollab.admin.service.RoomService;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    private OpenViduService openViduService;
    private RoomService roomService;

    @Autowired
    public SessionController(OpenViduService openViduService, RoomService roomService){
        this.openViduService = openViduService;
        this.roomService = roomService;
    }

    /**
     * Create a new room to start a shared video session.
     *
     * @param requestNewRoom request contains the name that will be displayed during the session.
     *
     * @return UUID to generate the link for all participants.
     */
    @PutMapping("/room")
    public ResponseEntity<ResponseNewRoom> createNewRoom(@RequestBody RequestNewRoom requestNewRoom){
        String roomName = requestNewRoom.getRoomName();
        String id = roomService.createNewRoom(roomName);
        logger.info("Created new room '{}' with UUID={}", roomName, id);
        return new ResponseEntity<>(new ResponseNewRoom(id), HttpStatus.OK);
    }

    /**
     * Join an existing room. Returns an error in case the room does not exists.
     *
     * @param requestJoinRoom  request contains the uuid of the room.
     *
     * @return response contains a token to start an openvidu session.
     */
    @PostMapping("/room/join")
    public ResponseEntity<ResponseJoinRoom> joinRoom(@RequestBody RequestJoinRoom requestJoinRoom) throws ResourceNotFoundException, OpenViduException {
        String roomUUID = requestJoinRoom.getRoomUUID();
        boolean exists = roomService.doesRoomExists(roomUUID);
        if(exists){
            Room room = roomService.findById(roomUUID);
            logger.debug("Found {}", room);
            Session session = openViduService.createSession(room.getName());
            if(session != null) {
                try {
                    logger.debug("Created session with id: {}", session.getSessionId());
                    String token = openViduService.getTokenForSession(session);
                    return new ResponseEntity<>(new ResponseJoinRoom(room.getName(), token), HttpStatus.OK);
                } catch (OpenViduJavaClientException | OpenViduHttpException e) {
                    logger.warn("Problem calling openvidu server.", e);
                    throw new OpenViduException("Problem calling openvidu server.");
                }
            } else {
                throw new ResourceNotFoundException("OpenVidu connection not working.");
            }
        } else {
            throw new ResourceNotFoundException("Room does not exists.");
        }
    }


}
