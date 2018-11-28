package com.jsrm.motor.pressure;

import com.jsrm.motor.GrainSurface;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import static java.lang.String.format;

public abstract class GrainEvolution {

    private static final String GRAIN_EVOLUTION_FORMULA = "initialCoreDiameter %s coreSurface * 2 * webRegression";

    private final WebRegression webRegression;

    private final Expression grainEvolutionExpression;

    public GrainEvolution(WebRegression webRegression, Evolution evolution) {
        this.webRegression = webRegression;

        grainEvolutionExpression = new ExpressionBuilder(format(GRAIN_EVOLUTION_FORMULA, evolution.getEvolutionSign()))
                .variables("initialCoreDiameter", "coreSurface", "webRegression")
                .build();
    }

    public double compute(int interval) {

        return grainEvolutionExpression
                .setVariable("initialCoreDiameter", getInitialDiameter())
                .setVariable("coreSurface", getSurface().value())
                .setVariable("webRegression", webRegression.compute(interval))
                .evaluate();
    }

    abstract GrainSurface getSurface();

    abstract double getInitialDiameter();

    WebRegression getWebRegression() {
        return webRegression;
    }

    public enum  Evolution {
        REGRESSION("-"),
        RISE("+");

        private final String evolutionSign;

        Evolution(String evolutionSign) {
            this.evolutionSign = evolutionSign;
        }

        public String getEvolutionSign() {
            return evolutionSign;
        }
    }
}
