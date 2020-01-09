package com.bmw.remotecollab.rest.v2.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseScreenToken {

    @ApiModelProperty(value = "The webRTC token to share your screen with the room.")
    private String screenToken;

}
