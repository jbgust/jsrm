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

        double substraction = gstar - aductDividedByThratArea;
        return substraction < 0 ? 0 : substraction;
    }
}
