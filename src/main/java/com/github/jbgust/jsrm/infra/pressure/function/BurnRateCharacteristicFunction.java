package com.github.jbgust.jsrm.infra.pressure.function;

import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.application.RegisteredPropellant;
import com.github.jbgust.jsrm.application.exception.UnregisteredPropellantException;
import com.github.jbgust.jsrm.calculation.exception.InvalidResultException;
import com.github.jbgust.jsrm.infra.function.NaNThrowExceptionFunction;

import static java.lang.Math.pow;

public class BurnRateCharacteristicFunction extends NaNThrowExceptionFunction {

    public BurnRateCharacteristicFunction() {
        super("BurnRateCharacteristic", 2);
    }

    @Override
    public double runFunction(double... doubles) {
        Double propellantId = doubles[0];
        double chamberPressure = doubles[1];

        try {
            SolidPropellant propellant = RegisteredPropellant.getSolidPropellant(propellantId.intValue());
            return propellant.getBurnRateCoefficient(chamberPressure) * pow(chamberPressure, propellant.getPressureExponent(chamberPressure));
        } catch (UnregisteredPropellantException e) {
            throw new InvalidResultException(e);
        }
    }
}
