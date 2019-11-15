package com.bmw.remotecollab.admin.rest.v1.requests;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestJoinRoom {

    @ApiModelProperty(value = "The UUID of the room, you want to join.", required = true)
    private String roomUUID;

}
