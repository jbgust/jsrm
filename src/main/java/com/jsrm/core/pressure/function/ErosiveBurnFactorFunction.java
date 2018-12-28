package com.jsrm.core.pressure.function;

import net.objecthunter.exp4j.function.Function;

public class ErosiveBurnFactorFunction extends Function{

    public ErosiveBurnFactorFunction() {
        super("ErosiveBurnFactor", 2);
    }

    @Override
    public double apply(double... doubles) {
        double aductDividedByThratArea = doubles[0];
        double gstar = doubles[1];

        double erosiveBurnFactor = gstar - aductDividedByThratArea;
        return erosiveBurnFactor < 0 ? 0 : erosiveBurnFactor;
    }
}
