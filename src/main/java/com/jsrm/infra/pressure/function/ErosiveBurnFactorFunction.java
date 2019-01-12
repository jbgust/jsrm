package com.jsrm.infra.pressure.function;

import com.jsrm.infra.function.NaNThrowExceptionFunction;

public class ErosiveBurnFactorFunction extends NaNThrowExceptionFunction{

    public ErosiveBurnFactorFunction() {
        super("ErosiveBurnFactor", 2);
    }

    @Override
    public double runFunction(double... doubles) {
        double aductDividedByThratArea = doubles[0];
        double gstar = doubles[1];

        double erosiveBurnFactor = gstar - aductDividedByThratArea;
        return erosiveBurnFactor < 0 ? 0 : erosiveBurnFactor;
    }
}
