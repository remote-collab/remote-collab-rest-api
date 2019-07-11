package com.bmw.remotecollab.admin.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room {

    private UUID id;
    private String name;
    private List<Member> members = new ArrayList<>();

    public Room(String name){
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '}';
    }
}
