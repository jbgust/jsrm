package com.jsrm.calculation;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.calculation.Formula.PREVIOUS_VARIABLE_SUFFIX;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

class LineCalculator {

    private final Formula formula;
    private final Map<String, Double> constants;
    private final Map<String, Double> initialValues;

    private Map<String, Double> previousLineResults;
    private Map<String, Double> currentLineResults;

    LineCalculator(Formula formula, Map<String, Double> constants, Map<String, Double> initialValues) {
        this.formula = formula;
        this.initialValues = new HashMap<>(initialValues);
        previousLineResults = new HashMap<>();
        this.constants = constants;
    }

    /**
     * Compute the results from the line
     * @param lineNumber the line number
     * @return the result of the line
     */
    Map<String, Double> compute(int lineNumber) {
        currentLineResults = new HashMap<>();

        run(formula, lineNumber);

        previousLineResults.clear();
        previousLineResults.putAll(currentLineResults.entrySet().stream()
                .collect(toMap(o-> o.getKey()+ PREVIOUS_VARIABLE_SUFFIX, Map.Entry::getValue)));

        return currentLineResults;
    }

    private void run(Formula formula, int lineNumber){

        if(hasInitialValue(formula)) {
            currentLineResults.put(formula.getName(), initialValues.remove(formula.getName()));
        } else {
            resolveVariablesDependencies(formula, lineNumber);

            double result = formula.getExpression()
                    .setVariables(getVariablesFromDependentCalculations(formula))
                    .setVariables(getPreviousVariables(formula))
                    .setVariables(constants)
                    .evaluate();

            currentLineResults.put(formula.getName(), result);
        }
    }

    private boolean hasInitialValue(Formula formula) {
        return initialValues.containsKey(formula.getName());
    }

    /**
     * Retrieve the variables from the previous line needed by the formula
     * @param formula the formula
     * @return the variables from the previous line needed by the formula
     */
    private Map<String, Double> getPreviousVariables(Formula formula) {
        return previousLineResults.entrySet().stream()
                .filter(resultatPrecEntry -> formula.getVariablesNames().contains(resultatPrecEntry.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Retrieve the variables from the formula dependencies
     * @param formula the formula
     * @return the variables from the formula dependencies
     */
    private Map<String, Double> getVariablesFromDependentCalculations(Formula formula) {
        return currentLineResults.entrySet().stream()
                .filter(entry -> formula.getDependencies().stream().map(Formula::getName).collect(toSet()).contains(entry.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Starts all the calculations on which the formula depends
     * @param formula the formula
     * @param lineNumber the line number
     */
    private void resolveVariablesDependencies(Formula formula, int lineNumber) {
        formula.getDependencies().stream()
                .filter(entry -> !currentLineResults.keySet().contains(entry.getName()))
                .forEach(formule1 -> run(formule1, lineNumber));
    }
}
