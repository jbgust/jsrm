package com.github.jbgust.jsrm.application.exception;

public class MotorClassificationOutOfBoundException extends JSRMException {
    public MotorClassificationOutOfBoundException() {
        super("The total impulse of this motor is not in [A;V] classes");
    }
}
