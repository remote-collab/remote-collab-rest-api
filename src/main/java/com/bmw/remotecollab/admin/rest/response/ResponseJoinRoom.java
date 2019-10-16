package com.bmw.remotecollab.admin.rest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseJoinRoom {
    private String roomName;
    private String token;
    private String secondToken;
    private String sessionId;
}
