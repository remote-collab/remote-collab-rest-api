package com.bmw.remotecollab.admin.rest.v2.responses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("ResponeNewRoomV2")
public class ResponseNewRoom {

    @ApiModelProperty(value = "The UUID of the room, you created.")
    private String uuid;

    private Date createdAt;

    private String name;
}
