package com.jsrm.infra.function;

import net.objecthunter.exp4j.function.Function;

public class CircleAreaFunction extends Function{

    public CircleAreaFunction() {
        super("CircleArea", 1);
    }

    @Override
    public double apply(double... doubles) {
        return Math.PI/4 * Math.pow(doubles[0], 2);
    }
}
