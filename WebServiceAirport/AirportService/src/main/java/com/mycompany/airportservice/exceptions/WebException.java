package com.mycompany.airportservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class WebException extends RuntimeException{
    public WebException(String path, String msg){
        super(String.format("%s %s", path, msg));
    }
}