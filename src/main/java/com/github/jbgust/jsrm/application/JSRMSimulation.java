package com.github.jbgust.jsrm.application;

import com.github.jbgust.jsrm.application.exception.SimulationFailedException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.grain.EndBurnerGrain;
import com.github.jbgust.jsrm.application.result.*;
import com.github.jbgust.jsrm.infra.ConstantsExtractor;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import com.github.jbgust.jsrm.infra.SolidRocketMotorChecker;
import com.github.jbgust.jsrm.infra.function.CircleAreaFunction;
import com.github.jbgust.jsrm.infra.performance.PerformanceCalculation;
import com.github.jbgust.jsrm.infra.performance.PerformanceCalculationResult;
import com.github.jbgust.jsrm.infra.performance.PerformanceResultProvider;
import com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.jbgust.jsrm.application.result.PortToThroatAreaWarning.fromPortToThroat;
import static com.github.jbgust.jsrm.infra.JSRMConstant.*;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;

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
     * @param customConfig JSRMConfig used to run the computation
     * @return The simuation result
     */
    public JSRMResult run(JSRMConfig customConfig) {
        this.config = customConfig;
        try {
            Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(motor, config);

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

        double portToThroatArea = computePortToThroatArea(motor, constants.get(at));
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
                averageThrust,
                constants.get(mgrain),
                chamberPressureResults.get(lowKNCorrection).stream().findFirst().orElse(0.0).longValue(),
                fromPortToThroat(portToThroatArea), portToThroatArea);
    }

    private double computePortToThroatArea(SolidRocketMotor motor, double throatArea) {
        double CombustionChamberCrossSectionnalArea = new CircleAreaFunction().runFunction(motor.getCombustionChamber().getChamberInnerDiameterInMillimeter());

        // For end grain we don't use the endGrainsurface because it doesn't make sense
        double grainEndSurface = motor.getPropellantGrain().getGrainConfigutation() instanceof EndBurnerGrain ?
                0 : motor.getPropellantGrain().getGrainConfigutation().getGrainEndSurface(0);

        double portArea = CombustionChamberCrossSectionnalArea - grainEndSurface;
        return portArea / throatArea;
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

        for(int i = 0; i < config.getLastCalcultationLine()+1; i++){
            motorParameters.add(new MotorParameters(
                    timeSinceBurnStartProvider.getResult(i),
                    performanceCalculationResult.getResults().get(PerformanceCalculation.Results.thrust).get(i),
                    chamberPressureResults.get(kn).get(i),
                    chamberPressureResults.get(ChamberPressureCalculation.Results.absoluteChamberPressure).get(i),
                    chamberPressureResults.get(ChamberPressureCalculation.Results.massFlowRate).get(i),
                    chamberPressureResults.get(ChamberPressureCalculation.Results.grainMass).get(i)));
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
