package com.bmw.remotecollab.admin.rest.v2.requests;

import com.bmw.remotecollab.admin.service.email.EmailList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("RequestNewRoomV2")
public class RequestNewRoom {

    @ApiModelProperty(value = "The name of the room, you want to create.", required = true)
    @NonNull
    private String roomName;

    @ApiModelProperty(value = "An optional list of email addresses you want to add initially to the new room.")
    @EmailList
    private List<String> emails;

}
