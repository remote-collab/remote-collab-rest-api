package com.bmw.remotecollab.admin.rest.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestNewRoom {

    private String roomName;
    private List<String> emails;

}
