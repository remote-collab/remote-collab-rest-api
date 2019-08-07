package com.bmw.remotecollab.admin.service.email;

public class From {

    private final String email;
    private final String name;

    public From(String email, String name) {
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
