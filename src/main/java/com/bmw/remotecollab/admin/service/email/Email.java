package com.bmw.remotecollab.admin.service.email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class Email {

    private List<String> to = new ArrayList<>();
    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();
    private From from;
    private String subject;
    private String body;
    private boolean html;

    public String getFrom() {
        return String.format("\"%s\" <%s>", from.getName(), from.getEmail());
    }

}
