package com.jsrm.motor.pressure;

import net.objecthunter.exp4j.ExpressionBuilder;

public class GrainCoreDiameter {

    private final WebRegression webRegression;

    public GrainCoreDiameter(WebRegression webRegression) {

        this.webRegression = webRegression;
    }

    public double compute(int interval) {
        return new ExpressionBuilder("initialCoreDiameter + coreSurface * 2 * webRegression")
                .variables("initialCoreDiameter", "coreSurface", "webRegression")
                .build()
                .setVariable("initialCoreDiameter", webRegression.getPropellantGrain().getCoreDiameter())
                .setVariable("coreSurface", webRegression.getPropellantGrain().getCoreSurface().value())
                .setVariable("webRegression", webRegression.compute(interval))
                .evaluate();
    }
}
