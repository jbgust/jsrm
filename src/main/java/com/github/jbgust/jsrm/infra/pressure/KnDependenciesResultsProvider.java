package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.calculation.ResultLineProvider;

import java.util.List;

public class KnDependenciesResultsProvider implements ResultLineProvider {
    private final String resultName;
    private final List<Double> results;

    public KnDependenciesResultsProvider(String variableName, List<Double> results) {
        this.resultName = variableName;
        this.results = results;
    }

    @Override
    public String getName() {
        return resultName;
    }

    @Override
    public double getResult(int lineNumber) {
        return results.get(lineNumber);
    }

    public double getSize() {
        return results.size();
    }
}
