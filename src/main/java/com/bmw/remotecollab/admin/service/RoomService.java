package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.dynamoDB.RoomRepository;
import com.bmw.remotecollab.admin.model.Member;
import com.bmw.remotecollab.admin.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Local implementation. No db storage up until now.
 */
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final EmailService emailService;

    @Autowired
    public RoomService(RoomRepository roomRepository, EmailService emailService) {
        this.roomRepository = roomRepository;
        this.emailService = emailService;
    }

    public String createNewRoom(String roomName, List<String> emails) {

        Room room = new Room(roomName);
        emails.forEach(s -> room.addMember(new Member(s)));

        roomRepository.save(room);
        String roomId = room.getId();
        emailService.sendInvitationEmail(roomId, room.getMembers());
        return roomId;
    }

    public boolean doesRoomExists(String id) {
        return roomRepository.existsById(id);
    }

    public Room findById(String roomUUID) {
        return roomRepository.findById(roomUUID).get();
    }
}
