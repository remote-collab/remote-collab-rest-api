package com.bmw.remotecollab.admin.service.email;

public enum From {

    NO_REPLY("noreply.viper-aws-ses@list.bmw.com", "Viper Remote Collab");

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
