package com.github.jbgust.jsrm.infra.pressure.function;

import com.github.jbgust.jsrm.infra.function.NaNThrowExceptionFunction;

public class NozzleMassFlowRateFunction extends NaNThrowExceptionFunction {

    public NozzleMassFlowRateFunction() {
        super("NozzleMassFlowRate", 4);
    }

    @Override
    public double runFunction(double... doubles) {
        double pbd = doubles[0];
        double mass_generation_rate = doubles[1];
        double chamber_pressure_previous = doubles[2];
        double ai = doubles[3];

        if (mass_generation_rate < ai) {
            if (chamber_pressure_previous > pbd) {
                return ai;
            } else {
                return 0;
            }
        } else {
            return ai;
        }
    }
}
