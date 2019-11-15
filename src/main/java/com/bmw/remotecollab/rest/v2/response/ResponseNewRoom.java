package com.bmw.remotecollab.rest.v2.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseNewRoom {

    @ApiModelProperty(value = "The UUID of the room, you created.")
    private String uuid;

    private Date createdAt;

    private String name;
}
