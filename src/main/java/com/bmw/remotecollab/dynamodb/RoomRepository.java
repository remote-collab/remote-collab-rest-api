package com.bmw.remotecollab.dynamodb;


import com.bmw.remotecollab.model.Room;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface RoomRepository extends CrudRepository<Room, String> {

}
