package com.bmw.remotecollab.admin.rest.v2.responses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("ResponseJoinRoomV2")
public class ResponseJoinRoom {
    @ApiModelProperty(value = "The name of the room, you wanted to join.")
    private String roomName;

    @ApiModelProperty(value = "The webRTC token to share your audio and video (Camera) with the room.")
    private String audioVideoToken;

    @ApiModelProperty(value = "The webRTC token to share your screen with the room.")
    private String screenShareToken;

    @ApiModelProperty(value = "The webRTC session id to join the room.")
    private String sessionId;
}
