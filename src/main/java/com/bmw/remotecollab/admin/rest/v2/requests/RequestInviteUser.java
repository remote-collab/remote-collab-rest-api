package com.bmw.remotecollab.admin.rest.v2.requests;

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

    @ApiModelProperty(value = "A non-null, non-empty list of valid email adresses.", required = true)
    @EmailList(emptyListIsValid = false)
    private List<String> emails;

}
