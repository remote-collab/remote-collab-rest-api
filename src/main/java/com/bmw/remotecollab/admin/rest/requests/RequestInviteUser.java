package com.bmw.remotecollab.admin.rest.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestInviteUser {

    private String roomUUID;
    private List<String> emails;

}
