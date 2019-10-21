package com.bmw.remotecollab.admin.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@DynamoDBTable(tableName = "sessionRoom")
@Getter
@Setter
@ToString
public class Room {

    @DynamoDBHashKey
    private String id;

    @DynamoDBAttribute(attributeName = "name")
    private String name;

    private Set<Member> members = new HashSet<>();

    public Room(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    // Needed for dynamoDB
    @SuppressWarnings("unused")
    public Room() {
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    public void addMembers(Collection<Member> members) {
        this.members.addAll(members);
    }
}
