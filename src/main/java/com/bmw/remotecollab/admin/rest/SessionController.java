package com.bmw.remotecollab.admin.rest;

import com.bmw.remotecollab.admin.rest.exception.OpenViduException;
import com.bmw.remotecollab.admin.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.admin.rest.requests.RequestInviteUser;
import com.bmw.remotecollab.admin.rest.requests.RequestJoinRoom;
import com.bmw.remotecollab.admin.rest.requests.RequestNewRoom;
import com.bmw.remotecollab.admin.rest.response.ResponseJoinRoom;
import com.bmw.remotecollab.admin.rest.response.ResponseNewRoom;
import com.bmw.remotecollab.admin.service.OpenViduService;
import com.bmw.remotecollab.admin.service.RoomService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/rooms")
    public ResponseEntity<ResponseNewRoom> createNewRoom(@RequestBody RequestNewRoom requestNewRoom){
        String roomName = requestNewRoom.getRoomName();
        List<String> emails = requestNewRoom.getEmails();
        String id = roomService.createNewRoom(roomName, emails);
        logger.info("Created new room '{}' with UUID={}", roomName, id);
        return new ResponseEntity<>(new ResponseNewRoom(id), HttpStatus.OK);
    }

    @PostMapping("/rooms/users")
    public ResponseEntity<String> inviteUser(@RequestBody RequestInviteUser requestInviteUser){
        String roomUUID = requestInviteUser.getRoomUUID();
        logger.debug(roomUUID);
        boolean exists = roomService.doesRoomExists(roomUUID);
        if(exists){
            List<String> emails = requestInviteUser.getEmails();
            roomService.sendUserInvitation(roomUUID, emails);
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    /**
     * Join an existing room. Returns an error in case the room does not exists.
     *
     * @param requestJoinRoom  request contains the uuid of the room.
     *
     * @return response contains a token to start an openvidu session.
     */
    @PostMapping("/rooms/join")
    public ResponseEntity<ResponseJoinRoom> joinRoom(@RequestBody RequestJoinRoom requestJoinRoom) throws ResourceNotFoundException, OpenViduException {
        String roomUUID = requestJoinRoom.getRoomUUID();
        logger.debug(roomUUID);
        ResponseJoinRoom responseJoinRoom = roomService.joinRoom(roomUUID);
        return new ResponseEntity<>(responseJoinRoom, HttpStatus.OK);
    }

    //TODO: replace with prometheus / actuator
    @GetMapping("/status")
    public String getStatus(){
        return "up";
    }

}
