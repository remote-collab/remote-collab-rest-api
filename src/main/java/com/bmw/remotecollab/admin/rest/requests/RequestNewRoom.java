package com.bmw.remotecollab.admin.rest.requests;

import com.bmw.remotecollab.admin.service.email.EmailList;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@Setter
public class RequestNewRoom {

    @ApiModelProperty(value = "The name of the room, you want to create.", required = true)
    @NonNull
    private String roomName;

    @ApiModelProperty(value = "An optional list of email addresses you want to add initially to the new room.")
    @EmailList
    private List<String> emails;

}
