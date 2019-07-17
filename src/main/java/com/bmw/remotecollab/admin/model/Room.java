package com.bmw.remotecollab.admin.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@DynamoDBTable(tableName = "sessionRoom")
@Getter
@Setter
@ToString

public class Room {

    @DynamoDBHashKey
    private String id;

    @DynamoDBAttribute(attributeName = "Name")
    private String name;

    private List<Member> members = new ArrayList<>();

    public Room(String name){
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public Room(){
    }

}
