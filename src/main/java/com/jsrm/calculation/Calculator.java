package com.jsrm.calculation;

import java.util.Map;
import java.util.stream.IntStream;

public class Calculator {

    private final Formula formula;
    private final Map<String, Double> constants;
    private final Map<Formula, Double> initialValues;
    private CalculatorResults calculatorResults;

    /**
     *
     * @param formula Formula that should be compute
     * @param constants Constants used in the formula and in its dependencies
     * @param initialValues Used by formulas that has a initial result
     */
    Calculator(Formula formula, Map<String, Double> constants, Map<Formula, Double> initialValues, Formula... resultsToSave) {
        this.formula = formula;
        this.constants = constants;
        this.initialValues = initialValues;
        calculatorResults = new CalculatorResults(resultsToSave);
    }

    /**
     *
     * @param fromLine first line of the calculation
     * @param toLine last line of the calculation
     * @return the result of the formula and its dependencies stored line by line. The result in each line are indexed by the formula name
     */
    public CalculatorResults compute(int fromLine, int toLine) {


        LineCalculator lineCalculator = new LineCalculator(formula, constants, initialValues);

        IntStream.range(fromLine, toLine)
                .mapToObj(lineCalculator::compute)
                .forEach(calculatorResults::addResult);

        return calculatorResults;
    }


}
