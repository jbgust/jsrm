package com.github.jbgust.jsrm.application.exception;

public class SimulationFailedException extends RuntimeException {
    public SimulationFailedException(Exception e) {
        super("Simulation failed", e);
    }
}
