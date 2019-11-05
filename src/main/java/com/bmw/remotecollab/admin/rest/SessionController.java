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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Valid
@RequestMapping("/api/v1")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    private OpenViduService openViduService;
    private RoomService roomService;

    @Autowired
    public SessionController(OpenViduService openViduService, RoomService roomService) {
        this.openViduService = openViduService;
        this.roomService = roomService;
    }

    /**
     * Create a new room to start a shared video session.
     *
     * @param requestNewRoom request contains the name that will be displayed during the session.
     * @return UUID to generate the link for all participants.
     */
    @PostMapping("/rooms")
    public ResponseEntity<ResponseNewRoom> createNewRoom(@RequestBody @Valid RequestNewRoom requestNewRoom) {
        String roomName = requestNewRoom.getRoomName();
        List<String> emails = requestNewRoom.getEmails();
        String roomUUID = roomService.createNewRoom(roomName, emails);
        logger.info("Created new room '{}' with UUID={}", roomName, roomUUID);
        return ResponseEntity.ok(new ResponseNewRoom(roomUUID));
    }

    @PostMapping("/rooms/users")
    public ResponseEntity inviteUser(@RequestBody @Valid RequestInviteUser requestInviteUser) throws ResourceNotFoundException {
        String roomUUID = requestInviteUser.getRoomUUID();
        logger.debug("Invite users to room {}", roomUUID);
        if (!roomService.doesRoomExist(roomUUID)) {
            throw new ResourceNotFoundException("Room does not exists. RoomUUID: " + roomUUID);
        }
        List<String> emails = requestInviteUser.getEmails();
        roomService.sendUserInvitation(roomUUID, emails);
        return ResponseEntity.ok().build();
    }

    /**
     * Join an existing room. Returns an error in case the room does not exists.
     *
     * @param requestJoinRoom request contains the uuid of the room.
     * @return response contains a token to start an openvidu session.
     */
    @PostMapping("/rooms/join")
    public ResponseEntity<ResponseJoinRoom> joinRoom(@RequestBody RequestJoinRoom requestJoinRoom) throws ResourceNotFoundException, OpenViduException {
        String roomUUID = requestJoinRoom.getRoomUUID();
        logger.debug("Join room {}", roomUUID);
        ResponseJoinRoom responseJoinRoom = roomService.joinRoom(roomUUID);
        return ResponseEntity.ok(responseJoinRoom);
    }

    //TODO: replace with prometheus / actuator
    @GetMapping("/status")
    public String getStatus() {
        return "up";
    }

}
