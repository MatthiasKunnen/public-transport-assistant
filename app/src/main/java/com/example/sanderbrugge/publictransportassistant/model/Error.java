package com.example.sanderbrugge.publictransportassistant.model;

/**
 * Created by sanderbrugge on 23/02/17.
 */

public class Error {
    private String message;
    private int code;

    public Error(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
