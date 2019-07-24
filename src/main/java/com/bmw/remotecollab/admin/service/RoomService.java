package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.dynamoDB.RoomRepository;
import com.bmw.remotecollab.admin.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Local implementation. No db storage up until now.
 */
@Service
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    public String createNewRoom(String roomName) {

        Room room = new Room(roomName);
        roomRepository.save(room);
        return room.getId();
    }

    public boolean doesRoomExists(String id) {
        return roomRepository.existsById(id);
    }

    public Room findById(String roomUUID) {
        return roomRepository.findById(roomUUID).get();
    }
}
