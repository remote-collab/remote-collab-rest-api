package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.dynamoDB.RoomRepository;
import com.bmw.remotecollab.admin.model.Member;
import com.bmw.remotecollab.admin.model.Room;
import com.bmw.remotecollab.admin.rest.exception.OpenViduException;
import com.bmw.remotecollab.admin.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.admin.rest.response.ResponseJoinRoom;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Local implementation. No db storage up until now.
 */
@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;
    private final EmailService emailService;
    private final OpenViduService openViduService;

    @Autowired
    public RoomService(RoomRepository roomRepository, EmailService emailService, OpenViduService openViduService) {
        this.roomRepository = roomRepository;
        this.emailService = emailService;
        this.openViduService = openViduService;
    }

    public String createNewRoom(String roomName, List<String> emails) {
        Room room = new Room(roomName);
        emails.forEach(s -> room.addMember(new Member(s)));

        roomRepository.save(room);
        String roomId = room.getId();
        emailService.sendInvitationEmail(roomId, room.getMembers());
        return roomId;
    }


    public ResponseJoinRoom joinRoom(String roomUUID) throws OpenViduException, ResourceNotFoundException {
        logger.debug(roomUUID);
        boolean exists = doesRoomExists(roomUUID);
        if(exists){
            Room room = findById(roomUUID);
            logger.debug("Found {}", room);
            Session session = openViduService.createSession(room.getId());
            if(session != null) {
                try {
                    logger.debug("Created session with id: {}", session.getSessionId());
                    String token = openViduService.getTokenForSession(session);
                    String secondToken = openViduService.getTokenForSession(session);
                    return new ResponseJoinRoom(room.getName(), token, secondToken, session.getSessionId());
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

    public boolean doesRoomExists(String id) {
        return roomRepository.existsById(id);
    }

    public Room findById(String roomUUID) {
        return roomRepository.findById(roomUUID).get();
    }
}
