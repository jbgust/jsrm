package com.jsrm.application;

import com.google.common.collect.ImmutableMap;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.application.motor.propellant.SolidPropellant;
import com.jsrm.application.result.JSRMResult;
import com.jsrm.application.result.MotorClassification;
import com.jsrm.application.result.Nozzle;
import com.jsrm.application.result.ThrustResult;
import com.jsrm.calculation.Formula;
import com.jsrm.infra.Extract;
import com.jsrm.infra.JSRMConstant;
import com.jsrm.infra.performance.PerformanceCalculation;
import com.jsrm.infra.performance.PerformanceCalculationResult;
import com.jsrm.infra.performance.PerformanceResultProvider;
import com.jsrm.infra.pressure.ChamberPressureCalculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.infra.JSRMConstant.*;
import static com.jsrm.infra.performance.PerformanceFormulas.*;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
import static com.jsrm.infra.pressure.PressureFormulas.*;

public class JSRMSimulation {

    private static final double GRAVITATIONAL_ACCELERATION = 9.806;

    private final SolidRocketMotor motor;
    private JSRMConfig config;

    public JSRMSimulation(SolidRocketMotor motor) {
        this.motor = motor;
        config = new JSRMConfig.Builder().createJSRMConfig();
    }

    public JSRMResult run(JSRMConfig config) {
        SolidPropellant propellant = motor.getPropellantGrain().getPropellant();

        Map<JSRMConstant, Double> constants = Extract.extractConstants(motor);
        // TODO: A calculer
        constants.put(cstar, 889.279521360202);

        // TODO: extraire les initials values proprement
        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(GRAIN_CORE_DIAMETER, motor.getPropellantGrain().getCoreDiameter());
        initialValues.put(GRAIN_OUTSIDE_DIAMETER, motor.getPropellantGrain().getOuterDiameter());
        initialValues.put(GRAIN_LENGTH, motor.getPropellantGrain().getGrainLength());
        initialValues.put(TEMPORARY_CHAMBER_PRESSURE, config.getAmbiantPressureInMPa());
        initialValues.put(TIME_SINCE_BURN_STARTS, 0d);
        initialValues.put(MASS_GENERATION_RATE, 0d);
        initialValues.put(NOZZLE_MASS_FLOW_RATE, 0d);
        initialValues.put(MASS_STORAGE_RATE, 0d);
        initialValues.put(MASS_COMBUSTION_PRODUCTS, 0d);
        initialValues.put(DENSITY_COMBUSTION_PRODUCTS, 0d);

        Map<ChamberPressureCalculation.Results, List<Double>> chamberPressureResults = new ChamberPressureCalculation(constants, initialValues).compute();

        PerformanceResultProvider chamberPressureProvider = new PerformanceResultProvider(chamberPressureMPA, chamberPressureResults.get(chamberPressureMPA));
        PerformanceResultProvider throatAreaProvider = new PerformanceResultProvider(throatArea, chamberPressureResults.get(throatArea));
        PerformanceResultProvider nozzleCriticalPassageAreaProvider = new PerformanceResultProvider(nozzleCriticalPassageArea, chamberPressureResults.get(nozzleCriticalPassageArea));
        PerformanceResultProvider timeSinceBurnStartProvider = new PerformanceResultProvider(timeSinceBurnStart, chamberPressureResults.get(timeSinceBurnStart));

        // TODO: extraire constante2
        Map<JSRMConstant, Double> constants2 = ImmutableMap.<JSRMConstant, Double>builder()
                .put(patm, config.getAmbiantPressureInMPa())
                .put(etanoz, config.getNozzleEfficiency())
                .put(k2ph, propellant.getK2Ph())
                .put(propellantId, constants.get(propellantId))
                .put(at, throatAreaProvider.getResult(0))
                .put(atfinal, throatAreaProvider.getResult((int) (throatAreaProvider.getSize()-1)))
                .build();

        // TODO : extraire les initials values proprement
        Map<Formula, Double> initialValues2 = ImmutableMap.<Formula, Double>builder()
                .put(OPTIMUM_NOZZLE_EXPANSION_RATIO, 1.0)
                .put(DELIVERED_THRUST_COEFFICIENT, constants2.get(etanoz))
                .put(THRUST, 0.0)
                .put(DELIVERED_IMPULSE, 0.0)
                .build();

        PerformanceCalculationResult performanceCalculationResult = new PerformanceCalculation(constants2, initialValues2,
                chamberPressureProvider, throatAreaProvider,
                nozzleCriticalPassageAreaProvider, timeSinceBurnStartProvider)
                .compute(config);

        double maxThrust = performanceCalculationResult.getResults().get(PerformanceCalculation.Results.thrust).stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        double maxChamberPressure = chamberPressureResults.get(ChamberPressureCalculation.Results.absoluteChamberPressure).stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        double totalImpulse = performanceCalculationResult.getResults().get(PerformanceCalculation.Results.deliveredImpulse).stream().mapToDouble(Double::doubleValue).sum();
        double thrustTime = getThrustTime(timeSinceBurnStartProvider);

        long averageThrust = Math.round(totalImpulse/thrustTime);

        Nozzle nozzle = new Nozzle(performanceCalculationResult.getOptimalNozzleExpansionResult(), 0,
                getNozzleExpansionRatioResult(config, performanceCalculationResult), 0,
                performanceCalculationResult.getInitialNozzleExitSpeedInMach(), performanceCalculationResult.getFinalNozzleExitSpeedInMach());

        //TODO calcule constants.get(vc)
        double grainMass = constants.get(rhopgrain) * 1575555.840 / 1000 / 1000;
        double specificImpulse = totalImpulse / GRAVITATIONAL_ACCELERATION / grainMass;

        List<ThrustResult> thrustResults = new ArrayList<>();
        for(int i = 0; i < 883; i++){
            thrustResults.add(new ThrustResult(
                    performanceCalculationResult.getResults().get(PerformanceCalculation.Results.thrust).get(i),
                    timeSinceBurnStartProvider.getResult(i)));
        }

        return new JSRMResult(maxThrust, totalImpulse, specificImpulse, maxChamberPressure, thrustTime,
                MotorClassification.getMotorClassification(totalImpulse), thrustResults, nozzle, averageThrust);
    }

    private double getNozzleExpansionRatioResult(JSRMConfig config, PerformanceCalculationResult performanceCalculationResult) {
        return config.isOptimalNozzleDesign()?performanceCalculationResult.getOptimalNozzleExpansionResult() : config.getNozzleExpansionRatio();
    }

    public JSRMResult run() {
        return run(config);
    }

    private double getThrustTime(PerformanceResultProvider timeSinceBurnStartProvider) {
        return timeSinceBurnStartProvider.getResult((int) (timeSinceBurnStartProvider.getSize()-2));
    }

    //TODO: nozzle desing result

}
