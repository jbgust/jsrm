package com.jsrm.calculation;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Calculator {

    private final Formula formula;
    private final Map<String, Double> constants;
    private final Map<String, Double> initialValues;

    /**
     *
     * @param formula Formula that should be compute
     * @param constants Constants used in the formula and in its dependencies
     * @param initialValues Used by formulas that has a initial result
     */
    public Calculator(Formula formula, Map<String, Double> constants, Map<String, Double> initialValues) {
        this.formula = formula;
        this.constants = constants;
        this.initialValues = initialValues;
    }

    /**
     *
     * @param fromLine first line of the calculation
     * @param toLine last line of the calculation
     * @return the result of the formula and its dependencies stored line by line. The result in each line are indexed by the formula name
     */
    public List<Map<String, Double>> compute(int fromLine, int toLine) {

        LineCalculator lineCalculator = new LineCalculator(formula, constants, initialValues);

        return IntStream.range(fromLine, toLine)
                .mapToObj(lineCalculator::compute)
                .collect(toList());
    }
}
