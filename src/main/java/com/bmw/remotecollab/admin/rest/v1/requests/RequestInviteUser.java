package com.bmw.remotecollab.admin.rest.v1.requests;

import com.bmw.remotecollab.admin.service.email.EmailList;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestInviteUser {

    @ApiModelProperty(value = "The UUID of the room, you want to invite people to.", required = true)
    private String roomUUID;

    @ApiModelProperty(value = "A non-null, non-empty list of valid email addresses.", required = true)
    @EmailList(emptyListIsValid = false)
    private List<String> emails;

}
