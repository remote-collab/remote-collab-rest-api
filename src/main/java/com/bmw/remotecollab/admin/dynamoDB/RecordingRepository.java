package com.bmw.remotecollab.admin.dynamoDB;


import com.bmw.remotecollab.admin.model.Recording;
import com.bmw.remotecollab.admin.model.Room;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface RecordingRepository extends CrudRepository<Recording, String> {

    Recording findByRecordingId(String recordingId);

}
