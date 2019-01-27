package com.github.jbgust.jsrm.infra.pressure.function;

import com.github.jbgust.jsrm.infra.function.NaNThrowExceptionFunction;

import static java.lang.Math.pow;

public class GrainMassFunction extends NaNThrowExceptionFunction{

    public GrainMassFunction() {
        super("GrainMass", 2);
    }

    @Override
    public double runFunction(double... doubles) {
        double grainDensity = doubles[0];
        double grainVolumeInCubicMillimeter = doubles[1];

        return grainDensity * grainVolumeInCubicMillimeter / pow(1000, 2);
    }
}
