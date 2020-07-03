package com.github.jbgust.jsrm.infra.pressure.function;

import com.github.jbgust.jsrm.infra.function.NaNThrowExceptionFunction;
import com.github.jbgust.jsrm.infra.pressure.solver.CoreMachSpeedSolver;

public class CoreMachNumberFunction extends NaNThrowExceptionFunction {

    public CoreMachNumberFunction() {
        super("coreMachNumberFunction", 2);
    }

    @Override
    public double runFunction(double... doubles) {
        double porToThroatAreaRatio = doubles[0];
        double k = doubles[1];

        return new CoreMachSpeedSolver(k).solve(porToThroatAreaRatio);
    }

}
