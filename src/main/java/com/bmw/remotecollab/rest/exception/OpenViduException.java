package com.bmw.remotecollab.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class OpenViduException extends Exception{

    private static final long serialVersionUID = 1L;

    public OpenViduException(String message){
        super(message);
    }
}
