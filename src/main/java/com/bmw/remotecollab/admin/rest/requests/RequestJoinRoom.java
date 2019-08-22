package com.bmw.remotecollab.admin.rest.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestJoinRoom {

    private String userName;
    private String roomUUID;

}
