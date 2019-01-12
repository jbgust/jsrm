package com.jsrm.infra.function;

public class HollowCircleAreaFunction extends NaNThrowExceptionFunction {

    public HollowCircleAreaFunction() {
        super("HollowCircleArea", 2);
    }

    @Override
    public double runFunction(double... doubles) {
        return Math.PI/4 * (Math.pow(doubles[0], 2) - Math.pow(doubles[1], 2));
    }
}
