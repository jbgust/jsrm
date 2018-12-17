package com.jsrm.pressure;

import net.objecthunter.exp4j.function.Function;

import static java.lang.Math.pow;

public class FreeVolumeInChamberFunction extends Function{

    public FreeVolumeInChamberFunction() {
        super("FreeVolumeInChamber", 2);
    }

    @Override
    public double apply(double... doubles) {
        double volumeChamber = doubles[0];
        double grainVolumeInMillimeter = doubles[1];

        return (volumeChamber - grainVolumeInMillimeter)/pow(1000,3);
    }
}
