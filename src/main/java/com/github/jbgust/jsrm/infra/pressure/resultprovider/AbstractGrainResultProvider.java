package com.github.jbgust.jsrm.infra.pressure.resultprovider;

import com.github.jbgust.jsrm.application.motor.propellant.GrainConfigutation;
import com.github.jbgust.jsrm.calculation.ResultLineProvider;

import static java.lang.Double.valueOf;

public abstract class AbstractGrainResultProvider implements ResultLineProvider {

    protected final GrainConfigutation grainConfigutation;
    protected final int numberLineDuringBurnCalculation;

    public AbstractGrainResultProvider(GrainConfigutation grainConfigutation, int numberLineDuringBurnCalculation) {
        this.grainConfigutation = grainConfigutation;
        this.numberLineDuringBurnCalculation = numberLineDuringBurnCalculation;
    }

    protected double getProgression(int lineNumber) {
        return valueOf(lineNumber)/valueOf(numberLineDuringBurnCalculation-1);
    }

}
