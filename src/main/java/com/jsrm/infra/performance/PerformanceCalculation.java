package com.jsrm.infra.performance;

import com.google.common.collect.ImmutableMap;
import com.jsrm.application.JSRMConfig;
import com.jsrm.calculation.CalculatorBuilder;
import com.jsrm.calculation.CalculatorResults;
import com.jsrm.calculation.Formula;
import com.jsrm.calculation.ResultLineProvider;
import com.jsrm.infra.ConstantsExtractor;
import com.jsrm.infra.JSRMConstant;
import com.jsrm.infra.performance.solver.MachSpeedAtNozzleExitSolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.calculation.Formula.PREVIOUS_VARIABLE_SUFFIX;
import static com.jsrm.infra.JSRMConstant.*;
import static com.jsrm.infra.RegisteredPropellant.getSolidPropellant;
import static com.jsrm.infra.performance.PerformanceCalculation.Results.deliveredImpulse;
import static com.jsrm.infra.performance.PerformanceCalculation.Results.thrust;
import static com.jsrm.infra.performance.PerformanceFormulas.*;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results.timeSinceBurnStart;

public class PerformanceCalculation {

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
        this.constants = new HashMap<>(constants);
        this.initialValues = new HashMap<>(initialValues);
        this.chamberPressureMpaResultProvider = chamberPressureMpaResultProvider;
        this.throatAreaResultProvider = throatAreaResultProvider;
        this.nozzleCriticalPassageAreaResultProvider = nozzleCriticalPassageAreaResultProvider;
        this.timeResultProvider = timeResultProvider;
    }

    public PerformanceCalculationResult compute(JSRMConfig config) {
        double optimalNozzleExpansionRatio = getOptimalNozzleExpansionRatio();
        computeNozzleExitSpeed(config, optimalNozzleExpansionRatio);

        CalculatorResults performanceResults = new CalculatorBuilder(DELIVERED_IMPULSE)
                .withConstants(ConstantsExtractor.toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultLineProviders(chamberPressureMpaResultProvider, throatAreaResultProvider, nozzleCriticalPassageAreaResultProvider, timeResultProvider)
                .withResultsToSave(THRUST, DELIVERED_IMPULSE)
                .createCalculator()
                .compute(START_CALCULATION_LINE, LAST_CALCULATION_LINE);

        performanceResults.addResult(computeLastLine(LAST_CALCULATION_LINE, performanceResults));

        return new PerformanceCalculationResult(ImmutableMap.<Results, List<Double>>builder()
               .put(thrust, performanceResults.getResults(THRUST))
               .put(deliveredImpulse, performanceResults.getResults(DELIVERED_IMPULSE))
               .build(), optimalNozzleExpansionRatio,
                constants.get(me), constants.get(mef));
    }

    private void computeNozzleExitSpeed(JSRMConfig config, double optimalNozzleExpansionRatio) {
        double initialNozzleExpansionRatio;
        initialNozzleExpansionRatio = computeInitialNozzleExpansionRatio(config, optimalNozzleExpansionRatio);

        double nozzleExitArea = constants.get(at) * initialNozzleExpansionRatio;
        MachSpeedAtNozzleExitSolver machSpeedAtNozzleExitSolver = new MachSpeedAtNozzleExitSolver(getSolidPropellant(constants.get(propellantId).intValue()));
        double finalNozzleExpansionRation = nozzleExitArea / constants.get(atfinal);

        constants.put(aexit, nozzleExitArea);
        constants.put(me, machSpeedAtNozzleExitSolver.solve(initialNozzleExpansionRatio));
        constants.put(mef, machSpeedAtNozzleExitSolver.solve(finalNozzleExpansionRation));
        initialValues.put(MACH_SPEED_AT_NOZZLE_EXIT, constants.get(me));
    }

    private double computeInitialNozzleExpansionRatio(JSRMConfig config, double optimalNozzleExpansionRatio) {
        double initialNozzleExpansionRatio;
        if(config.isOptimalNozzleDesign()){
            initialNozzleExpansionRatio = optimalNozzleExpansionRatio;
        } else {
            initialNozzleExpansionRatio = config.getNozzleExpansionRatio();
        }
        return initialNozzleExpansionRatio;
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

    private double getOptimalNozzleExpansionRatio() {
        List<Double> optimalNozzleRatioByTime = new CalculatorBuilder(OPTIMUM_NOZZLE_EXPANSION_RATIO)
                .withConstants(ConstantsExtractor.toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultLineProviders(chamberPressureMpaResultProvider)
                .createCalculator()
                .compute(START_CALCULATION_LINE, LAST_CALCULATION_LINE)
                .getResults(OPTIMUM_NOZZLE_EXPANSION_RATIO);
        return optimalNozzleRatioByTime.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    }

    public enum Results {
        thrust,
        deliveredImpulse
    }
}
