package com.bmw.remotecollab.admin.rest.requests;

import com.bmw.remotecollab.admin.service.email.EmailList;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@Setter
public class RequestNewRoom {

    @NonNull
    private String roomName;

    @EmailList
    @NonNull
    private List<String> emails;

}
