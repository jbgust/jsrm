package com.jsrm.infra.function;

public class CircleAreaFunction extends NaNThrowExceptionFunction {

    public CircleAreaFunction() {
        super("CircleArea", 1);
    }

    @Override
    public double runFunction(double... doubles) {
        return Math.PI/4 * Math.pow(doubles[0], 2);
    }
}
