package com.bmw.remotecollab.admin.rest;

public class CreateSessionRequest {

    public String customSessionId;

    public String getCustomSessionId() {
        return customSessionId;
    }

    public void setCustomSessionId(String customSessionId) {
        this.customSessionId = customSessionId;
    }

    @Override
    public String toString() {
        return "CreateSessionRequest{" +
                "customSessionId='" + customSessionId + '\'' +
                '}';
    }
}
