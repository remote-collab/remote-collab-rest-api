package com.bmw.remotecollab.rest.requests;

import com.bmw.remotecollab.service.email.EmailList;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestInviteUser {

    @ApiModelProperty(value = "The UUID of the room, you want to invite people to.", required = true)
    private String roomUUID;

    @ApiModelProperty(value = "A non-null, non-empty list of valid email adresses.", required = true)
    @EmailList(emptyListIsValid = false)
    private List<String> emails;

}
