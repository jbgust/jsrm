package com.jsrm.application;

import com.google.common.collect.ImmutableMap;
import com.jsrm.application.exception.SimulationFailedException;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.application.result.JSRMResult;
import com.jsrm.application.result.MotorClassification;
import com.jsrm.application.result.Nozzle;
import com.jsrm.application.result.ThrustResult;
import com.jsrm.infra.ConstantsExtractor;
import com.jsrm.infra.JSRMConstant;
import com.jsrm.infra.performance.PerformanceCalculation;
import com.jsrm.infra.performance.PerformanceCalculationResult;
import com.jsrm.infra.performance.PerformanceResultProvider;
import com.jsrm.infra.pressure.ChamberPressureCalculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jsrm.infra.JSRMConstant.*;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;

public class JSRMSimulation {
    private final SolidRocketMotor motor;

    private JSRMConfig config;

    public JSRMSimulation(SolidRocketMotor motor) {
        this.motor = motor;
        config = new JSRMConfig.Builder().createJSRMConfig();
    }

    public JSRMResult run() {
        return run(config);
    }

    public JSRMResult run(JSRMConfig config) {
        try {
            Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(motor, new JSRMConfig.Builder().createJSRMConfig());

            Map<ChamberPressureCalculation.Results, List<Double>> chamberPressureResults = new ChamberPressureCalculation(motor, config, constants).compute();

            PerformanceResultProvider chamberPressureProvider = new PerformanceResultProvider(chamberPressureMPA, chamberPressureResults.get(chamberPressureMPA));
            PerformanceResultProvider throatAreaProvider = new PerformanceResultProvider(throatArea, chamberPressureResults.get(throatArea));
            PerformanceResultProvider nozzleCriticalPassageAreaProvider = new PerformanceResultProvider(nozzleCriticalPassageArea, chamberPressureResults.get(nozzleCriticalPassageArea));
            PerformanceResultProvider timeSinceBurnStartProvider = new PerformanceResultProvider(timeSinceBurnStart, chamberPressureResults.get(timeSinceBurnStart));

            Map<JSRMConstant, Double> performanceConstants = ImmutableMap.<JSRMConstant, Double>builder()
                    .putAll(constants)
                    .put(atfinal, throatAreaProvider.getResult((int) (throatAreaProvider.getSize()-1)))
                    .build();


            PerformanceCalculationResult performanceCalculationResult = new PerformanceCalculation(motor, performanceConstants,
                    chamberPressureProvider, throatAreaProvider,
                    nozzleCriticalPassageAreaProvider, timeSinceBurnStartProvider)
                    .compute(config);

            return buildResult(config, constants, chamberPressureResults, timeSinceBurnStartProvider, performanceCalculationResult);
        } catch (Exception e) {
           throw new SimulationFailedException(e);
        }
    }

    private JSRMResult buildResult(JSRMConfig config, Map<JSRMConstant, Double> constants, Map<ChamberPressureCalculation.Results, List<Double>> chamberPressureResults, PerformanceResultProvider timeSinceBurnStartProvider, PerformanceCalculationResult performanceCalculationResult) {
        double maxThrust = performanceCalculationResult.getResults().get(PerformanceCalculation.Results.thrust).stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        double maxChamberPressure = chamberPressureResults.get(ChamberPressureCalculation.Results.absoluteChamberPressure).stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        double totalImpulse = performanceCalculationResult.getResults().get(PerformanceCalculation.Results.deliveredImpulse).stream().mapToDouble(Double::doubleValue).sum();
        double thrustTime = getThrustTime(timeSinceBurnStartProvider);
        long averageThrust = Math.round(totalImpulse/thrustTime);
        double specificImpulse = getSpecificImpulse(constants, totalImpulse);

        return new JSRMResult(
                maxThrust,
                totalImpulse,
                specificImpulse,
                maxChamberPressure,
                thrustTime,
                MotorClassification.getMotorClassification(totalImpulse),
                buildThrustResult(timeSinceBurnStartProvider, performanceCalculationResult),
                buildNozzleResult(config, performanceCalculationResult),
                averageThrust);
    }

    private Nozzle buildNozzleResult(JSRMConfig config, PerformanceCalculationResult performanceCalculationResult) {
        return new Nozzle(performanceCalculationResult.getOptimalNozzleExpansionResult(), performanceCalculationResult.getOptimalNozzleExitDiameterInMillimeter(),
                getNozzleExpansionRatioResult(config, performanceCalculationResult), performanceCalculationResult.getNozzleExitDiameterInMillimeter(),
                performanceCalculationResult.getInitialNozzleExitSpeedInMach(), performanceCalculationResult.getFinalNozzleExitSpeedInMach());
    }

    private List<ThrustResult> buildThrustResult(PerformanceResultProvider timeSinceBurnStartProvider, PerformanceCalculationResult performanceCalculationResult) {
        List<ThrustResult> thrustResults = new ArrayList<>();

        for(int i = 0; i < LAST_CALCULATION_LINE+1; i++){
            thrustResults.add(new ThrustResult(
                    performanceCalculationResult.getResults().get(PerformanceCalculation.Results.thrust).get(i),
                    timeSinceBurnStartProvider.getResult(i)));
        }
        return thrustResults;
    }

    private double getSpecificImpulse(Map<JSRMConstant, Double> constants, double totalImpulse) {
        return totalImpulse / GRAVITATIONAL_ACCELERATION / constants.get(mgrain);
    }

    private double getNozzleExpansionRatioResult(JSRMConfig config, PerformanceCalculationResult performanceCalculationResult) {
        return config.isOptimalNozzleDesign()?performanceCalculationResult.getOptimalNozzleExpansionResult() : config.getNozzleExpansionRatio();
    }

    private double getThrustTime(PerformanceResultProvider timeSinceBurnStartProvider) {
        return timeSinceBurnStartProvider.getResult((int) (timeSinceBurnStartProvider.getSize()-2));
    }

}
