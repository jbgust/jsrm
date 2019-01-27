package com.github.jbgust.jsrm.calculation.exception;

public class InvalidResultException extends RuntimeException {
    public InvalidResultException(String message) {
        super(message);
    }

    public InvalidResultException(Throwable throwable) {
        super(throwable);
    }
}
