package com.bmw.remotecollab.admin.model;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class Member {
    @Email
    private String email;

    public Member(String email) {
        this.email = email;
    }

    @SuppressWarnings("unused")
    public Member() {
    }
}
