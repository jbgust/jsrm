package com.jsrm.infra.function;

import net.objecthunter.exp4j.function.Function;

public class HollowCircleAreaFunction extends Function{

    public HollowCircleAreaFunction() {
        super("HollowCircleArea", 2);
    }

    @Override
    public double apply(double... doubles) {
        return Math.PI/4 * (Math.pow(doubles[0], 2) - Math.pow(doubles[1], 2));
    }
}
