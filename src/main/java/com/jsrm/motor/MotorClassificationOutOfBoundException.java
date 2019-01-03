package com.jsrm.motor;

public class MotorClassificationOutOfBoundException extends RuntimeException {
    public MotorClassificationOutOfBoundException() {
        super("The total impulse of this motor is not in [A;V] classes");
    }
}
