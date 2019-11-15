package com.bmw.remotecollab.rest.v1.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("ResponseJoinRoomV1")
public class ResponseJoinRoom {
    @ApiModelProperty(value = "The name of the room, you wanted to join.")
    private String roomName;

    @ApiModelProperty(value = "The webRTC token to share your audio and video (Camera) with the room.")
    private String token;

    @ApiModelProperty(value = "The webRTC token to share your screen with the room.")
    private String secondToken;

    @ApiModelProperty(value = "The webRTC session id to join the room.")
    private String sessionId;
}
