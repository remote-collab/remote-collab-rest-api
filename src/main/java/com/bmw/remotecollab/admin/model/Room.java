package com.bmw.remotecollab.admin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class Room {

    private UUID id;
    private String name;
    private List<Member> members = new ArrayList<>();

    public Room(String name){
        this.id = UUID.randomUUID();
        this.name = name;
    }

}
