package com.jsrm.infra.performance;

import com.google.common.collect.ImmutableMap;
import com.jsrm.calculation.CalculatorBuilder;
import com.jsrm.calculation.CalculatorResults;
import com.jsrm.calculation.Formula;
import com.jsrm.calculation.ResultLineProvider;
import com.jsrm.infra.JSRMConstant;
import com.jsrm.infra.Extract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.calculation.Formula.PREVIOUS_VARIABLE_SUFFIX;
import static com.jsrm.infra.performance.PerformanceCalculation.Results.deliveredImpulse;
import static com.jsrm.infra.performance.PerformanceCalculation.Results.thrust;
import static com.jsrm.infra.performance.PerformanceFormulas.DELIVERED_IMPULSE;
import static com.jsrm.infra.performance.PerformanceFormulas.OPTIMUM_NOZZLE_EXPANSION_RATIO;
import static com.jsrm.infra.performance.PerformanceFormulas.THRUST;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results.timeSinceBurnStart;

public class PerformanceCalculation {

    private static final int NB_LINE_IN_PERFORMANCE_SPREADSHEET = 835;
    private static final Double NB_LINE_POST_BURN_CALCULATION = 47d;

    private static final int START_LINE = 0;

    private final Map<JSRMConstant, Double> constants;
    private final Map<Formula, Double> initialValues;
    private final ResultLineProvider chamberPressureMpaResultProvider;
    private final ResultLineProvider throatAreaResultProvider;
    private final ResultLineProvider nozzleCriticalPassageAreaResultProvider;
    private final ResultLineProvider timeResultProvider;

    public PerformanceCalculation(Map<JSRMConstant, Double> constants, Map<Formula, Double> initialValues,
                                  ResultLineProvider chamberPressureMpaResultProvider,
                                  ResultLineProvider throatAreaResultProvider,
                                  ResultLineProvider nozzleCriticalPassageAreaResultProvider,
                                  ResultLineProvider timeResultProvider) {
        this.constants = constants;
        this.initialValues = initialValues;
        this.chamberPressureMpaResultProvider = chamberPressureMpaResultProvider;
        this.throatAreaResultProvider = throatAreaResultProvider;
        this.nozzleCriticalPassageAreaResultProvider = nozzleCriticalPassageAreaResultProvider;
        this.timeResultProvider = timeResultProvider;
    }

    public Map<Results, List<Double>> compute() {
        int lastLine = NB_LINE_IN_PERFORMANCE_SPREADSHEET + NB_LINE_POST_BURN_CALCULATION.intValue();

        //TODO : USING ONLY FOR FINDING OPTIMAL_NOZZLE_RATIO
        getOptimalNozzleRatio(lastLine);

        CalculatorResults performanceResults = new CalculatorBuilder(DELIVERED_IMPULSE)
                .withConstants(Extract.toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultLineProviders(chamberPressureMpaResultProvider, throatAreaResultProvider, nozzleCriticalPassageAreaResultProvider, timeResultProvider)
                .withResultsToSave(THRUST, DELIVERED_IMPULSE)
                .createCalculator()
                .compute(START_LINE, lastLine);

        performanceResults.addResult(computeLastLine(lastLine, performanceResults));

       return ImmutableMap.<Results, List<Double>>builder()
               .put(thrust, performanceResults.getResults(THRUST))
               .put(deliveredImpulse, performanceResults.getResults(DELIVERED_IMPULSE))
               .build();
    }

    private Map<Formula, Double> computeLastLine(int lastLine, CalculatorResults performanceResults) {
        Map<Formula, Double> lastResultLine = new HashMap<>();
        lastResultLine.put(THRUST, 0.0);
        lastResultLine.put(DELIVERED_IMPULSE, DELIVERED_IMPULSE.getExpression()
                .setVariable(THRUST.name(), 0.0)
                .setVariable(THRUST.name() + PREVIOUS_VARIABLE_SUFFIX, performanceResults.getResult(THRUST, lastLine-1))
                .setVariable(timeSinceBurnStart.name(), timeResultProvider.getResult(lastLine))
                .setVariable(timeSinceBurnStart.name()+ PREVIOUS_VARIABLE_SUFFIX, timeResultProvider.getResult(lastLine-1))
                .evaluate());
        return lastResultLine;
    }

    private void getOptimalNozzleRatio(int lastLine) {
        List<Double> optimalNozzleRatioByTime = new CalculatorBuilder(OPTIMUM_NOZZLE_EXPANSION_RATIO)
                .withConstants(Extract.toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultLineProviders(chamberPressureMpaResultProvider)
                .createCalculator()
                .compute(START_LINE, lastLine)
                .getResults(OPTIMUM_NOZZLE_EXPANSION_RATIO);
        Double optimalNozzleRatioAtPoMax = optimalNozzleRatioByTime.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        Double averageOptimalNozzleRatio = optimalNozzleRatioByTime.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    }

    public enum Results {
        thrust,
        deliveredImpulse
    }
}
