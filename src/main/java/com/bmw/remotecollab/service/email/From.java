package com.bmw.remotecollab.service.email;

public class From {

    private final String email;
    private final String name;

    public From(String email, String name) {
        this.email =email;
        this.name = name;
    }

    String getEmail() {
        return email;
    }

    String getName() {
        return name;
    }
}
