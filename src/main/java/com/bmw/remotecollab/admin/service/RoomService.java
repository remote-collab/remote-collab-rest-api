package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.dynamoDB.RoomRepository;
import com.bmw.remotecollab.admin.model.Member;
import com.bmw.remotecollab.admin.model.Room;
import com.bmw.remotecollab.admin.rest.exception.OpenViduException;
import com.bmw.remotecollab.admin.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.admin.service.email.EmailList;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Local implementation. No db storage up until now.
 */
@Service
@Validated
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

    public Room createNewRoom(String roomName, List<String> emails) {

        Room room = new Room(roomName);

        if (emails != null) {
            emails.forEach(s -> room.addMember(new Member(s)));
        }

        roomRepository.save(room);
        emailService.sendInvitationEmail(room);
        return room;
    }


    public JoinRoomTokens joinRoom(String roomUUID) throws OpenViduException, ResourceNotFoundException {
        logger.debug(roomUUID);

        final Optional<Room> roomOpt = roomRepository.findById(roomUUID);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            logger.debug("Found {}", room);
            Session session = openViduService.createSession(room.getId());
            if (session != null) {
                try {
                    logger.debug("Created session with id: {}", session.getSessionId());
                    String audioVideoToken = openViduService.getTokenForSession(session);

                    String screenShareToken = openViduService.getTokenForSession(session);
                    return new JoinRoomTokens(room.getName(), audioVideoToken, screenShareToken, session.getSessionId());
                } catch (OpenViduJavaClientException | OpenViduHttpException e) {
                    logger.warn("Problem calling openvidu server.", e);
                    throw new OpenViduException("Problem calling openvidu server.");
                }
            } else {
                throw new ResourceNotFoundException("OpenVidu connection not working.");
            }
        } else {
            throw new ResourceNotFoundException("Room does not exists. RoomUUID: " + roomUUID);
        }
    }

    public boolean doesRoomExist(String roomUUID) {
        return roomRepository.existsById(roomUUID);
    }

    public void sendUserInvitation(String roomUUID, @EmailList(emptyListIsValid = false) List<String> emails) {
        Set<Member> newMembers = emails.stream().map(Member::new).collect(Collectors.toSet());

        roomRepository.findById(roomUUID)
                .ifPresent(r -> {
                    emailService.sendInvitationEmail(r);
                    r.addMembers(newMembers);
                    roomRepository.save(r);
                });
    }


    @RequiredArgsConstructor
    public static class JoinRoomTokens {
        public final String roomName;
        public final String audioVideoToken;
        public final String screenShareToken;
        public final String sessionId;
    }
}