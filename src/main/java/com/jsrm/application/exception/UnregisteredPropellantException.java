package com.jsrm.application.exception;

public class UnregisteredPropellantException extends Throwable {

    public UnregisteredPropellantException(int propellantId) {
        super("The propellant with id ("+propellantId+") is not registered.\n " +
                "Use native propellant cf. PropellantType.class or register your propellant with " +
                "com.jsrm.infra.RegisteredPropellant.registerPropellant(solidPropellant)");
    }
}
