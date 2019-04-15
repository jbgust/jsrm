package com.github.jbgust.jsrm.application;

import static com.github.jbgust.jsrm.infra.JSRMConstant.GRAVITATIONAL_ACCELERATION;
import static com.github.jbgust.jsrm.infra.JSRMConstant.LAST_CALCULATION_LINE;
import static com.github.jbgust.jsrm.infra.JSRMConstant.atfinal;
import static com.github.jbgust.jsrm.infra.JSRMConstant.mgrain;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.chamberPressureMPA;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.kn;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.nozzleCriticalPassageArea;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.throatArea;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.timeSinceBurnStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.jbgust.jsrm.application.exception.SimulationFailedException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorClassification;
import com.github.jbgust.jsrm.application.result.MotorParameters;
import com.github.jbgust.jsrm.application.result.Nozzle;
import com.github.jbgust.jsrm.infra.ConstantsExtractor;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import com.github.jbgust.jsrm.infra.SolidRocketMotorChecker;
import com.github.jbgust.jsrm.infra.performance.PerformanceCalculation;
import com.github.jbgust.jsrm.infra.performance.PerformanceCalculationResult;
import com.github.jbgust.jsrm.infra.performance.PerformanceResultProvider;
import com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation;
import com.google.common.collect.ImmutableMap;

public class JSRMSimulation {

    private final SolidRocketMotor motor;

    private JSRMConfig config;

    /**
     * Create a JSRMSimulation for a motor
     * @param motor the motor that will be used in simulation
     */
    public JSRMSimulation(SolidRocketMotor motor) {
        SolidRocketMotorChecker.check(motor);
        this.motor = motor;
        config = new JSRMConfigBuilder().createJSRMConfig();
    }

    /**
     * Run the simulation with default configuration
     * @return The simuation result
     */
    public JSRMResult run() {
        return run(config);
    }

    /**
     * Run the simulation with the given configuration
     * @param config JSRMConfig used ti run the computation
     * @return The simuation result
     */
    public JSRMResult run(JSRMConfig config) {
        try {
            Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(motor, new JSRMConfigBuilder().createJSRMConfig());

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
        double averageChamberPressure = chamberPressureResults.get(ChamberPressureCalculation.Results.absoluteChamberPressure).stream().mapToDouble(Double::doubleValue).average().getAsDouble();
        double totalImpulse = performanceCalculationResult.getResults().get(PerformanceCalculation.Results.deliveredImpulse).stream().mapToDouble(Double::doubleValue).sum();
        double thrustTime = getThrustTime(timeSinceBurnStartProvider);
        long averageThrust = Math.round(totalImpulse/thrustTime);
        double specificImpulse = getSpecificImpulse(constants, totalImpulse);

        return new JSRMResult(
                maxThrust,
                totalImpulse,
                specificImpulse,
                maxChamberPressure,
                averageChamberPressure,
                thrustTime,
                MotorClassification.getMotorClassification(totalImpulse),
                buildMotorParametersResult(timeSinceBurnStartProvider, chamberPressureResults, performanceCalculationResult),
                buildNozzleResult(config, performanceCalculationResult),
                averageThrust);
    }

    private Nozzle buildNozzleResult(JSRMConfig config, PerformanceCalculationResult performanceCalculationResult) {
        return new Nozzle(motor.getThroatDiameterInMillimeter(), motor.getCombustionChamber().getChamberInnerDiameterInMillimeter(), performanceCalculationResult.getOptimalNozzleExpansionResult(), performanceCalculationResult.getOptimalNozzleExitDiameterInMillimeter(),
                getNozzleExpansionRatioResult(config, performanceCalculationResult), performanceCalculationResult.getNozzleExitDiameterInMillimeter(),
                performanceCalculationResult.getInitialNozzleExitSpeedInMach(), performanceCalculationResult.getFinalNozzleExitSpeedInMach());
    }

    private List<MotorParameters> buildMotorParametersResult(PerformanceResultProvider timeSinceBurnStartProvider,
                                                             Map<ChamberPressureCalculation.Results, List<Double>> chamberPressureResults,
                                                             PerformanceCalculationResult performanceCalculationResult) {
        List<MotorParameters> motorParameters = new ArrayList<>();

        for(int i = 0; i < LAST_CALCULATION_LINE+1; i++){
            motorParameters.add(new MotorParameters(
                    timeSinceBurnStartProvider.getResult(i),
                    performanceCalculationResult.getResults().get(PerformanceCalculation.Results.thrust).get(i),
                    chamberPressureResults.get(kn).get(i),
                    chamberPressureResults.get(ChamberPressureCalculation.Results.absoluteChamberPressure).get(i),
                    chamberPressureResults.get(ChamberPressureCalculation.Results.massFlowRate).get(i)));
        }
        return motorParameters;
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
