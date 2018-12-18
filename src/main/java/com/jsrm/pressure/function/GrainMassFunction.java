package com.jsrm.pressure.function;

import net.objecthunter.exp4j.function.Function;

import static java.lang.Math.pow;

public class GrainMassFunction extends Function{

    public GrainMassFunction() {
        super("GrainMass", 2);
    }

    @Override
    public double apply(double... doubles) {
        double grainDensity = doubles[0];
        double grainVolumeInCubicMillimeter = doubles[1];

        return grainDensity * grainVolumeInCubicMillimeter / pow(1000, 2);
    }
}
