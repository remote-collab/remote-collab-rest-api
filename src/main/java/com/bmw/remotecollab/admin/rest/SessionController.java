package com.bmw.remotecollab.admin.rest;

import com.bmw.remotecollab.admin.model.Recording;
import com.bmw.remotecollab.admin.rest.exception.OpenViduException;
import com.bmw.remotecollab.admin.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.admin.rest.requests.RequestJoinRoom;
import com.bmw.remotecollab.admin.rest.requests.RequestNewRoom;
import com.bmw.remotecollab.admin.rest.response.ResponseJoinRoom;
import com.bmw.remotecollab.admin.rest.response.ResponseNewRoom;
import com.bmw.remotecollab.admin.rest.response.ResponseStartRecording;
import com.bmw.remotecollab.admin.service.RecordingService;
import com.bmw.remotecollab.admin.service.RoomService;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
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

    private RoomService roomService;
    private RecordingService recordingService;

    @Autowired
    public SessionController(RoomService roomService, RecordingService recordingService){
        this.roomService = roomService;
        this.recordingService = recordingService;
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
        logger.info("{} is joining room {}", requestJoinRoom.getUserName(), requestJoinRoom.getRoomUUID());
        ResponseJoinRoom responseJoinRoom = roomService.joinRoom(roomUUID);
        return new ResponseEntity<>(responseJoinRoom, HttpStatus.OK);
    }

    @PostMapping("/rooms/{roomUUID}/recordings")
    public ResponseEntity<ResponseStartRecording> startRecording(@PathVariable String roomUUID, @RequestParam(value = "action") String action) throws OpenViduJavaClientException, OpenViduHttpException, ResourceNotFoundException {
        logger.info("startRecording session {}. Action: {}", roomUUID, action);
        ResponseStartRecording response = this.recordingService.startRecording(roomUUID);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/rooms/{roomUUID}/recordings/{recordingId}")
    public ResponseEntity<Recording> stopRecordingWithRoom(@PathVariable String roomUUID, @PathVariable String recordingId, @RequestParam(value = "action") String action) throws OpenViduJavaClientException, OpenViduHttpException, ResourceNotFoundException {
        logger.info("stopRecording recording {} for room {}. Action: {}", recordingId, roomUUID, action);
        return internalStopRecording(recordingId);
    }

    @PostMapping("/recordings/{recordingId}")
    public ResponseEntity<Recording> stopRecording(@PathVariable String recordingId, @RequestParam(value = "action") String action) throws OpenViduJavaClientException, OpenViduHttpException, ResourceNotFoundException {
        logger.info("stopRecording recording {}. Action: {}", recordingId, action);
        return internalStopRecording(recordingId);
    }

    private ResponseEntity<Recording> internalStopRecording(String recordingId) throws OpenViduJavaClientException, OpenViduHttpException, ResourceNotFoundException {
        Recording recording = this.recordingService.stopRecording(recordingId);
        return new ResponseEntity<>(recording, HttpStatus.OK);
    }
}
