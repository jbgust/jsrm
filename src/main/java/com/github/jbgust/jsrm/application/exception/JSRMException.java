package com.github.jbgust.jsrm.application.exception;

public class JSRMException extends RuntimeException {
    public JSRMException(String message) {
        super(message);
    }

    public JSRMException(String message, Exception e) {
        super(message, e);
    }
}
