package com.bmw.remotecollab.admin.rest.requests;

import com.bmw.remotecollab.admin.service.email.EmailList;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestInviteUser {

    private String roomUUID;
    @EmailList(emptyListIsValid = false)
    private List<String> emails;

}
