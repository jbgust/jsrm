package com.jsrm.calculation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.jsrm.calculation.Formula.PREVIOUS_VARIABLE_SUFFIX;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;

public class LineCalculator {

    private final Formula formula;
    private final Map<String, Double> constants;
    private final Map<Formula, Double> initialValues;
    private final Set<ResultLineProvider> resultLineProviders;

    private Map<String, Double> previousLineResults;
    private Map<Formula, Double> currentLineResults;
    private Map<String, Double> currentLineProvidedResult;

    public LineCalculator(Formula formula, Map<String, Double> constants, Map<Formula, Double> initialValues) {
        this(formula, constants, initialValues, emptySet());
    }

    public LineCalculator(Formula formula, Map<String, Double> constants, Map<Formula, Double> initialValues, Set<ResultLineProvider> resultLineProviders) {
        this.formula = formula;
        this.initialValues = new HashMap<>(initialValues);
        this.resultLineProviders = resultLineProviders;
        previousLineResults = new HashMap<>();
        this.constants = constants;
    }

    /**
     * Compute the results for the line
     * @param lineNumber the line number
     * @return the result of the line
     */
    public Map<Formula, Double> compute(int lineNumber) {
        currentLineResults = new HashMap<>();

        storeProvidedResults(lineNumber);

        run(formula, lineNumber);

        addPreviousValues();

        return currentLineResults;
    }

    private void storeProvidedResults(int lineNumber) {
        currentLineProvidedResult = resultLineProviders.stream()
                .collect(toMap(ResultLineProvider::getName, resultLineProvider -> resultLineProvider.getResult(lineNumber)));
    }

    private void addPreviousValues() {
        previousLineResults.clear();

        previousLineResults.putAll(currentLineResults.entrySet().stream()
                .collect(toMap(o-> o.getKey().getName()+ PREVIOUS_VARIABLE_SUFFIX, Map.Entry::getValue)));

        previousLineResults.putAll(currentLineProvidedResult.entrySet().stream()
                .collect(toMap(o-> o.getKey()+ PREVIOUS_VARIABLE_SUFFIX, Map.Entry::getValue)));
    }

    private void run(Formula formula, int lineNumber){

        resolveVariablesDependencies(formula, lineNumber);

        if(hasInitialValue(formula)) {
            currentLineResults.put(formula, initialValues.remove(formula));
        } else {
            try {
                double result = formula.getExpression()
                        .setVariables(currentLineProvidedResult)
                        .setVariables(getVariablesFromDependentCalculations(formula))
                        .setVariables(getPreviousVariables(formula))
                        .setVariables(constants)
                        .evaluate();

                currentLineResults.put(formula, result);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean hasInitialValue(Formula formula) {
        return initialValues.containsKey(formula);
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
                .filter(entry -> formula.getDependencies().contains(entry.getKey()))
                .collect(toMap(entry -> entry.getKey().getName(), Map.Entry::getValue));
    }

    /**
     * Starts all the calculations on which the formula depends
     * @param formula the formula
     * @param lineNumber the line number
     */
    private void resolveVariablesDependencies(Formula formula, int lineNumber) {
        formula.getDependencies().stream()
                .filter(entry -> !currentLineResults.keySet().contains(entry))
                .forEach(formule1 -> run(formule1, lineNumber));
    }
}
