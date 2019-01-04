package com.jsrm.infra.performance;

import com.jsrm.calculation.ResultLineProvider;
import com.jsrm.infra.pressure.ChamberPressureCalculation;

import java.util.List;

public class PerformanceResultProvider implements ResultLineProvider {

    private final String resultName;
    private final List<Double> results;

    public PerformanceResultProvider(ChamberPressureCalculation.Results result, List<Double> results) {
        this.resultName = result.name();
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
}
