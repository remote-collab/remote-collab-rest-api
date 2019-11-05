package com.bmw.remotecollab.admin.rest.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseNewRoom {

    @ApiModelProperty(value = "The UUID of the room, you created.")
    private String uuid;

}
