package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.dynamoDB.RoomRepository;
import com.bmw.remotecollab.admin.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local implementation. No db storage up until now.
 */
@Service
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    private static Map<UUID, Room> localRoomCache = new ConcurrentHashMap<>();

    public String createNewRoom(String roomName){

        Room room = new Room(roomName);
        roomRepository.save(room);
        this.localRoomCache.put(room.getId(), room);
        return room.getId().toString();
    }

    public boolean doesRoomExists(String id){
        return this.localRoomCache.containsKey(UUID.fromString(id));
    }

    public Room findById(String roomUUID) {

            return roomRepository.findById(roomUUID).get();


//        return this.localRoomCache.get(UUID.fromString(roomUUID));
    }
}
