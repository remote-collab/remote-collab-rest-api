package com.bmw.remotecollab.rest.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("ResponseNewRoomV1")
public class ResponseNewRoom {

    @ApiModelProperty(value = "The UUID of the room, you created.")
    private String uuid;

}
