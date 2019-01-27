package com.github.jbgust.jsrm.application.exception;

public class UnregisteredPropellantException extends RuntimeException {

    public UnregisteredPropellantException(int propellantId) {
        super("The propellant with id ("+propellantId+") is not registered.\n " +
                "Use native propellant cf. PropellantType.class or register your propellant with " +
                "RegisteredPropellant.registerPropellant(solidPropellant)");
    }
}
