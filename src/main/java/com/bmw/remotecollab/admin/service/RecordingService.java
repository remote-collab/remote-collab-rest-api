package com.bmw.remotecollab.admin.service;

import com.bmw.remotecollab.admin.dynamoDB.RecordingRepository;
import com.bmw.remotecollab.admin.model.Recording;
import com.bmw.remotecollab.admin.model.Room;
import com.bmw.remotecollab.admin.rest.exception.ResourceNotFoundException;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RecordingService {

    private static final Logger logger = LoggerFactory.getLogger(RecordingService.class);

    @Autowired
    private RoomService roomService;
    @Autowired
    private OpenViduService openViduService;
    @Autowired
    private RecordingRepository recordingRepository;

    public String startRecording(String roomUUID) throws OpenViduJavaClientException, OpenViduHttpException, ResourceNotFoundException {
        logger.info("startRecording session {}", roomUUID);
        boolean exists = roomService.doesRoomExists(roomUUID);
        if(exists) {
            Room room = roomService.findById(roomUUID);
            String recordingId = this.openViduService.startRecording(room);
            Recording recording = new Recording();
            recording.setRecordingId(recordingId);
            room.addRecording(recording);
            recordingRepository.save(recording);
            return recordingId;
        }
        throw new ResourceNotFoundException("Room does not exists.");
    }

    public Recording stopRecording(String recordingId) throws OpenViduJavaClientException, OpenViduHttpException {
        Recording recording = recordingRepository.findByRecordingId(recordingId);

        this.openViduService.stopRecording(recordingId);
        recording.setStopped(new Date());
        return this.recordingRepository.save(recording);

    }
}
