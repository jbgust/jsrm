package com.github.jbgust.jsrm.infra.performance.function;

import com.github.jbgust.jsrm.infra.function.NaNThrowExceptionFunction;

public class SafeMinValueFunction extends NaNThrowExceptionFunction {
    public SafeMinValueFunction() {
        super("SafeMinValue", 3);
    }

    @Override
    public double runFunction(double... doubles) {
        double value = doubles[0];
        double minAllowed = doubles[1];
        double replacementValue = doubles[2];

        if(value < minAllowed) {
            return replacementValue;
        } else {
            return value;
        }
    }
}
