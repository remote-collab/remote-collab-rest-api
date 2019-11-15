package com.bmw.remotecollab.service.email;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class Email {

    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private From from;
    private String subject;
    private String body;
    private boolean html;

    String getFrom() {
        return String.format("\"%s\" <%s>", from.getName(), from.getEmail());
    }

}
