package com.jsrm.calculation;

import java.util.*;

import static com.google.common.collect.Sets.newHashSet;

public class CalculatorResults {

    private Map<Formula, List<Double>> results;
    private Set<Formula> resultsToSave;

    public CalculatorResults(Formula ... formulasToSave) {
        this.resultsToSave = newHashSet(formulasToSave);
        this.results = new HashMap<>();

        resultsToSave.forEach(formula -> results.put(formula, new ArrayList<>()));
    }

    public void addResult(Map<Formula, Double> lineResult) {
        lineResult.forEach(this::saveResult);
    }

    public Double getResult(Formula formula, int lineNumber) {
        checkResultExists(formula);
        return results.get(formula).get(lineNumber);
    }

    private void saveResult(Formula formula, Double resultat) {
        if(resultsToSave.contains(formula)){
            results.get(formula).add(resultat);
        }
    }

    public List<Double> getResults(Formula formula) {
        checkResultExists(formula);
        return results.get(formula);
    }

    private void checkResultExists(Formula formula) throws UnkownResultException {
        if(!resultsToSave.contains(formula)) {
            throw new UnkownResultException(formula);
        }
    }
}
