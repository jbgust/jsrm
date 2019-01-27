package com.github.jbgust.jsrm.infra.performance;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.calculation.CalculatorBuilder;
import com.github.jbgust.jsrm.calculation.CalculatorResults;
import com.github.jbgust.jsrm.calculation.ResultLineProvider;
import com.github.jbgust.jsrm.infra.ConstantsExtractor;
import com.github.jbgust.jsrm.infra.performance.solver.MachSpeedAtNozzleExitSolver;
import com.google.common.collect.ImmutableMap;
import com.github.jbgust.jsrm.calculation.Formula;
import com.github.jbgust.jsrm.infra.JSRMConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.jbgust.jsrm.calculation.Formula.PREVIOUS_VARIABLE_SUFFIX;
import static com.github.jbgust.jsrm.infra.JSRMConstant.*;
import static com.github.jbgust.jsrm.infra.RegisteredPropellant.getSolidPropellant;
import static com.github.jbgust.jsrm.infra.performance.PerformanceCalculation.Results.deliveredImpulse;
import static com.github.jbgust.jsrm.infra.performance.PerformanceCalculation.Results.thrust;
import static com.github.jbgust.jsrm.infra.performance.PerformanceFormulas.*;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.timeSinceBurnStart;

public class PerformanceCalculation {

    private final SolidRocketMotor motor;
    private final Map<JSRMConstant, Double> constants;
    private final Map<Formula, Double> initialValues;
    private final ResultLineProvider chamberPressureMpaResultProvider;
    private final ResultLineProvider throatAreaResultProvider;
    private final ResultLineProvider nozzleCriticalPassageAreaResultProvider;
    private final ResultLineProvider timeResultProvider;

    public PerformanceCalculation(SolidRocketMotor motor, Map<JSRMConstant, Double> constants,
                                  ResultLineProvider chamberPressureMpaResultProvider,
                                  ResultLineProvider throatAreaResultProvider,
                                  ResultLineProvider nozzleCriticalPassageAreaResultProvider,
                                  ResultLineProvider timeResultProvider) {
        this.motor = motor;
        this.constants = new HashMap<>(constants);
        this.initialValues = getInitialValues(constants);
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
                constants.get(me), constants.get(mef),
                Math.sqrt(optimalNozzleExpansionRatio)*motor.getThroatDiameterInMillimeter(),
                Math.sqrt(4*constants.get(aexit)/Math.PI));//=RACINE(4*aexit/PI())
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
        if (config.isOptimalNozzleDesign()) {
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
                .setVariable(THRUST.name() + PREVIOUS_VARIABLE_SUFFIX, performanceResults.getResult(THRUST, lastLine - 1))
                .setVariable(timeSinceBurnStart.name(), timeResultProvider.getResult(lastLine))
                .setVariable(timeSinceBurnStart.name() + PREVIOUS_VARIABLE_SUFFIX, timeResultProvider.getResult(lastLine - 1))
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

    private Map<Formula, Double> getInitialValues(Map<JSRMConstant, Double> constants) {
        Map<Formula, Double> initialvalues = new HashMap<>();
        initialvalues.put(OPTIMUM_NOZZLE_EXPANSION_RATIO, 1.0);
        initialvalues.put(DELIVERED_THRUST_COEFFICIENT, constants.get(etanoz));
        initialvalues.put(THRUST, 0.0);
        initialvalues.put(DELIVERED_IMPULSE, 0.0);
        return initialvalues;
    }

    public enum Results {
        thrust,
        deliveredImpulse;
    }
}
