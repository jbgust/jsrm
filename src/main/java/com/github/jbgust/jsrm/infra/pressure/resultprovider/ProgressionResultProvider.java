package com.github.jbgust.jsrm.infra.pressure.resultprovider;

public class ProgressionResultProvider extends AbstractGrainResultProvider {

    public static final String PROGRESSION_VARIABLE = "progression";

    public ProgressionResultProvider(int numberLineDuringBurnCalculation) {
        super(null, numberLineDuringBurnCalculation);
    }

    @Override
    public String getName() {
        return PROGRESSION_VARIABLE;
    }

    @Override
    public double getResult(int lineNumber) {
        return getProgression(lineNumber);
    }
}
