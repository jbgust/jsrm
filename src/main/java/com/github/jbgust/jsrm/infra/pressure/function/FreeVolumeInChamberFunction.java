package com.github.jbgust.jsrm.infra.pressure.function;

import com.github.jbgust.jsrm.infra.function.NaNThrowExceptionFunction;

import static java.lang.Math.pow;

public class FreeVolumeInChamberFunction extends NaNThrowExceptionFunction{

    public FreeVolumeInChamberFunction() {
        super("FreeVolumeInChamber", 2);
    }

    @Override
    public double runFunction(double... doubles) {
        double volumeChamber = doubles[0];
        double grainVolumeInCubicMillimeter = doubles[1];

        return (volumeChamber - grainVolumeInCubicMillimeter)/pow(1000,3);
    }
}
