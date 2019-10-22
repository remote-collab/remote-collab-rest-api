package com.bmw.remotecollab.admin.rest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseJoinRoom {
    private String roomName;

    //TODO: Rename after ITFair to audioVideoToken
    private String token;

    //TODO: Rename after ITFair to screenShareToken
    private String secondToken;

    private String sessionId;
}
