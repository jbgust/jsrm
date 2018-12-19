package com.jsrm.motor.propellant;

public class ChamberPressureOutOfBoundException extends RuntimeException {

    public ChamberPressureOutOfBoundException(String message) {
        super(message);
    }
}
