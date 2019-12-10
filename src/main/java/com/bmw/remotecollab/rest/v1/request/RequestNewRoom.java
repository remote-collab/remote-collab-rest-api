package com.bmw.remotecollab.rest.v1.request;

import com.bmw.remotecollab.service.email.EmailList;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestNewRoom {

    @ApiModelProperty(value = "The name of the room, you want to create.", required = true)
    @NonNull
    private String roomName;

    @ApiModelProperty(value = "An optional list of email addresses you want to add initially to the new room.")
    @EmailList
    @Nullable
    private List<String> emails = new ArrayList<>();

}
