package com.github.jbgust.jsrm.calculation;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

public class CalculatorBuilder {
    private Formula formula;
    private Map<String, Double> constants = emptyMap();
    private Map<Formula, Double> initialValues = emptyMap();
    private Formula[] resultsToSave;
    private Set<ResultLineProvider> resultLineProviders = emptySet();

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

    public CalculatorBuilder withResultLineProviders(ResultLineProvider... resultLineProviders) {
        this.resultLineProviders = newHashSet(resultLineProviders);
        return this;
    }

    public Calculator createCalculator() {
        return new Calculator(formula, constants, initialValues, resultLineProviders, resultsToSave);
    }
}