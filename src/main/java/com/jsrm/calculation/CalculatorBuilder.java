package com.jsrm.calculation;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class CalculatorBuilder {
    private Formula formula;
    private Map<String, Double> constants = emptyMap();
    private Map<Formula, Double> initialValues = emptyMap();
    private Formula[] resultsToSave;

    public CalculatorBuilder(Formula formula) {
        this.formula = formula;
        resultsToSave = new Formula[]{formula};
    }

    public CalculatorBuilder withConstants(Map<String, Double> constants) {
        this.constants = constants;
        return this;
    }

    public CalculatorBuilder withInitialValues(Map<Formula, Double> initialValues) {
        this.initialValues = initialValues;
        return this;
    }

    public CalculatorBuilder withResultsToSave(Formula... resultsToSave) {
        this.resultsToSave = resultsToSave;
        return this;
    }

    public Calculator createCalculator() {
        return new Calculator(formula, constants, initialValues, resultsToSave);
    }
}