package com.bmw.remotecollab.rest.v2;

import com.bmw.remotecollab.model.Room;
import com.bmw.remotecollab.rest.exception.OpenViduException;
import com.bmw.remotecollab.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.rest.v2.request.RequestInviteUser;
import com.bmw.remotecollab.rest.v2.request.RequestNewRoom;
import com.bmw.remotecollab.rest.v2.response.ResponseJoinRoom;
import com.bmw.remotecollab.rest.v2.response.ResponseNewRoom;
import com.bmw.remotecollab.rest.v2.response.ResponseScreenToken;
import com.bmw.remotecollab.service.OpenViduService;
import com.bmw.remotecollab.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@Api(tags = {"/rooms"}, produces = MediaType.APPLICATION_JSON_VALUE)
@ApiResponses({
        @ApiResponse(code = 400, message = "Validation of parameter failed")})
@RequestMapping("/api/v2")
public class RoomControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(RoomControllerV2.class);

    private OpenViduService openViduService;
    private RoomService roomService;

    @Autowired
    public RoomControllerV2(OpenViduService openViduService, RoomService roomService) {
        this.openViduService = openViduService;
        this.roomService = roomService;
    }


    @ApiOperation(value = "Create a new room to start a shared remote collaboration session.",
            notes = "Via this endpoint, you can create a new room for audio/video/screen sharing."
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", response = ResponseNewRoom.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/rooms")
    public ResponseEntity<ResponseNewRoom> createNewRoom(@RequestBody @Valid RequestNewRoom requestNewRoom) {
        String roomName = requestNewRoom.getRoomName();
        List<String> emails = requestNewRoom.getEmails();
        Room room = roomService.createNewRoom(roomName, emails);
        logger.info("V2: Created new room '{}'", room);
        return ResponseEntity.ok(new ResponseNewRoom(room.getId(), room.getCreatedAt(), room.getName()));
    }

    @ApiOperation(hidden = true,
            value = "Send invitation emails to users for an existing remote collaboration room.",
            notes = "Via this endpoint, you can send invitation emails to users which should participate in the " +
                    "remote collaboration session.")
    @PostMapping("/rooms/{roomUUID}/users")
    public ResponseEntity<Void> inviteUser(@PathVariable("roomUUID") final String roomUUID, @RequestBody @Valid RequestInviteUser requestInviteUser) throws ResourceNotFoundException {
        logger.debug("V2: Invite users to room {}", roomUUID);
        if (!roomService.doesRoomExist(roomUUID)) {
            throw new ResourceNotFoundException("Room does not exists. RoomUUID: " + roomUUID);
        }
        List<String> emails = requestInviteUser.getEmails();
        roomService.sendUserInvitation(roomUUID, emails);
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "Join an existing room.",
            notes = "Via this endpoint, you can acquire tokens to join a webRTC session for an existing room.")
    @PostMapping("/rooms/{roomUUID}/join")
    public ResponseEntity<ResponseJoinRoom> joinRoom(@PathVariable("roomUUID") final String roomUUID) throws ResourceNotFoundException, OpenViduException {
        logger.debug("V2: Join room {}", roomUUID);
        final RoomService.JoinRoomToken tokenInfo = roomService.joinRoom(roomUUID);
        return ResponseEntity.ok(
                new ResponseJoinRoom(
                        tokenInfo.roomName,
                        tokenInfo.audioVideoToken,
                        tokenInfo.sessionId));
    }

    @ApiOperation(value = "Get new screen token for existing room.",
            notes = "Via this endpoint, you can acquire tokens to join a webRTC session for an existing room.")
    @GetMapping("/rooms/{roomUUID}/screentoken")
    public ResponseEntity<ResponseScreenToken> getScreenToken(@PathVariable("roomUUID") final String roomUUID) throws ResourceNotFoundException, OpenViduException {
        logger.debug("V2: Get new screentoken for room {}", roomUUID);
        final RoomService.ScreenToken tokenInfo = roomService.requestSessionToken(roomUUID);
        return ResponseEntity.ok(new ResponseScreenToken(tokenInfo.screenShareToken));
    }

    //TODO: replace with prometheus / actuator
    @GetMapping("/status")
    public String getStatus() {
        return "up";
    }

}
