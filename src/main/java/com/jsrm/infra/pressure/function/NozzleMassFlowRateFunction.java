package com.jsrm.infra.pressure.function;

import net.objecthunter.exp4j.function.Function;

public class NozzleMassFlowRateFunction extends Function {

    public NozzleMassFlowRateFunction() {
        super("NozzleMassFlowRate", 4);
    }

    @Override
    public double apply(double... doubles) {
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
