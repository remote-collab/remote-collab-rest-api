package com.bmw.remotecollab.rest.requests;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestJoinRoom {

    @ApiModelProperty(value = "The UUID of the room, you want to join.", required = true)
    private String roomUUID;

}
