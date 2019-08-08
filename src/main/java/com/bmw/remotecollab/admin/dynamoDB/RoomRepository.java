package com.bmw.remotecollab.admin.dynamoDB;


import com.bmw.remotecollab.admin.model.Room;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface RoomRepository extends CrudRepository<Room, String> {

}
