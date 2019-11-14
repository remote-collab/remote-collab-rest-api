package com.bmw.remotecollab.rest.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseJoinRoom {
    @ApiModelProperty(value = "The name of the room, you wanted to join.")
    private String roomName;

    //TODO: Rename after ITFair to audioVideoToken
    @ApiModelProperty(value = "The webRTC token to share your audio and video (Camera) with the room.")
    private String token;

    //TODO: Rename after ITFair to screenShareToken
    @ApiModelProperty(value = "The webRTC token to share your screen with the room.")
    private String secondToken;

    @ApiModelProperty(value = "The webRTC session id to join the room.")
    private String sessionId;
}
