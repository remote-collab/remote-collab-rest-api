package com.bmw.remotecollab.admin.service.email;

public enum From {

    NO_REPLY("noreply@mysestest.com", "My SES Test"),
    SUPPORT("support@mysestest.com", "My SES Support Support");

    private final String email;
    private final String name;

    From(String email, String name) {
        this.email =email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
