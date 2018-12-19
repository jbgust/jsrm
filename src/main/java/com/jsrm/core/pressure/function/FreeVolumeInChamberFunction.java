package com.jsrm.core.pressure.function;

import net.objecthunter.exp4j.function.Function;

import static java.lang.Math.pow;

public class FreeVolumeInChamberFunction extends Function{

    public FreeVolumeInChamberFunction() {
        super("FreeVolumeInChamber", 2);
    }

    @Override
    public double apply(double... doubles) {
        double volumeChamber = doubles[0];
        double grainVolumeInCubicMillimeter = doubles[1];

        return (volumeChamber - grainVolumeInCubicMillimeter)/pow(1000,3);
    }
}
