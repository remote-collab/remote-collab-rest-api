package com.bmw.remotecollab.admin.service;

import com.amazonaws.Response;
import com.bmw.remotecollab.admin.dynamoDB.RecordingRepository;
import com.bmw.remotecollab.admin.model.Recording;
import com.bmw.remotecollab.admin.model.Room;
import com.bmw.remotecollab.admin.rest.exception.ResourceNotFoundException;
import com.bmw.remotecollab.admin.rest.response.ResponseStartRecording;
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

    public ResponseStartRecording startRecording(String roomUUID) throws OpenViduJavaClientException, OpenViduHttpException, ResourceNotFoundException {
        boolean exists = roomService.doesRoomExists(roomUUID);
        if(exists) {
            Room room = roomService.findById(roomUUID);
            String recordingId = this.openViduService.startRecording(room);
            logger.info("Started new recording for {}. RecordingID: {}", room.getName(), recordingId);
            Recording recording = new Recording();
            recording.setRecordingId(recordingId);
            room.addRecording(recording);
            recordingRepository.save(recording);
            return  new ResponseStartRecording(recordingId );
        }
        throw new ResourceNotFoundException("Room does not exists.");
    }

    public Recording stopRecording(String recordingId) throws OpenViduJavaClientException, OpenViduHttpException, ResourceNotFoundException {
        Recording recording = recordingRepository.findByRecordingId(recordingId);
        if(recording == null){
            throw new ResourceNotFoundException("Recording does not exists.");
        }

        this.openViduService.stopRecording(recordingId);
        recording.setStopped(new Date());
        return this.recordingRepository.save(recording);

    }
}
