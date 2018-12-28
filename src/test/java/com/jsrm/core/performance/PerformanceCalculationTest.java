package com.jsrm.core.performance;

import com.google.common.collect.ImmutableMap;
import com.jsrm.calculation.Formula;
import com.jsrm.core.JSRMConstant;
import com.jsrm.core.performance.csv.CsvToPerformanceLine;
import com.jsrm.core.pressure.ChamberPressureCalculation;
import com.jsrm.infra.Extract;
import com.jsrm.motor.MotorChamber;
import com.jsrm.motor.PropellantGrain;
import com.jsrm.motor.SolidRocketMotor;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.core.JSRMConstant.*;
import static com.jsrm.core.performance.PerformanceCalculation.Results.deliveredImpulse;
import static com.jsrm.core.performance.PerformanceCalculation.Results.thrust;
import static com.jsrm.core.performance.PerformanceFormulas.*;
import static com.jsrm.core.pressure.ChamberPressureCalculation.Results;
import static com.jsrm.core.pressure.ChamberPressureCalculation.Results.*;
import static com.jsrm.core.pressure.PressureFormulas.*;
import static com.jsrm.motor.GrainSurface.EXPOSED;
import static com.jsrm.motor.GrainSurface.INHIBITED;
import static com.jsrm.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class PerformanceCalculationTest {

    private Map<PerformanceCalculation.Results, Offset> precisionByResults = ImmutableMap.<PerformanceCalculation.Results, Offset>builder()
            .put(thrust, offset(1.0))
            .put(deliveredImpulse, offset(0.001))
            .build();

    private Map<PerformanceCalculation.Results, String> formulasByResult = ImmutableMap.of(
            thrust, THRUST.getName(),
            deliveredImpulse, DELIVERED_IMPULSE.getName()
    );

    private static Map<PerformanceCalculation.Results, List<Double>> performanceResults;
    private static int lineNumber = 0;
    private static PerformanceResultProvider chamberPressureProvider;
    private static PerformanceResultProvider throatAreaProvider;
    private static PerformanceResultProvider nozzleCriticalPassageAreaProvider;
    private static PerformanceResultProvider timeSinceBurnStartProvider;

    @BeforeAll
    static void init(){
        fillResultProviders();

        Map<JSRMConstant, Double> constants = ImmutableMap.<JSRMConstant, Double>builder()
                .put(patm, 0.101)
                .put(etanoz, 0.85)
                .put(k2ph, KNDX.getK2Ph())
                .put(aexit, 1901.974657752680)
                .put(me, 2.95455756202289)
                .put(mef, 2.95455756202289)
                .build();

        Map<Formula, Double> initialValues = ImmutableMap.<Formula, Double>builder()
                .put(OPTIMUM_NOZZLE_EXPANSION_RATIO, 1.0)
                .put(DELIVERED_THRUST_COEFFICIENT, constants.get(etanoz))
                .put(THRUST, 0.0)
                .put(DELIVERED_IMPULSE, 0.0)
                .put(MACH_SPEED_AT_NOZZLE_EXIT, constants.get(me))
                .build();

        performanceResults = new PerformanceCalculation(constants, initialValues,
                chamberPressureProvider, throatAreaProvider,
                nozzleCriticalPassageAreaProvider, timeSinceBurnStartProvider)
                .compute();

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_PERFORMANCE_QUALIFICATION.csv", numLinesToSkip = 2, delimiter = '|')
    @DisplayName("Check performance with SRM results")
    void qualification(@CsvToPerformanceLine Map<String, Double> performanceLine) {

        precisionByResults.forEach((result, offset) ->
                assertThat(performanceResults.get(result).get(lineNumber))
                        .as(result.name())
                        .isEqualTo(performanceLine.getOrDefault(formulasByResult.get(result), -111.0), offset));
        lineNumber++;
    }

    private static void fillResultProviders() {
        Map<Formula, Double> initialValuesChamberPressure = new HashMap<>();
        initialValuesChamberPressure.put(GRAIN_CORE_DIAMETER, 20d);
        initialValuesChamberPressure.put(GRAIN_OUTSIDE_DIAMETER, 69d);
        initialValuesChamberPressure.put(GRAIN_LENGTH, 460d);

        //TODO
        initialValuesChamberPressure.put(TIME_SINCE_BURN_STARTS, 0d);
        initialValuesChamberPressure.put(TEMPORARY_CHAMBER_PRESSURE, 0.101);
        initialValuesChamberPressure.put(MASS_GENERATION_RATE, 0d);
        initialValuesChamberPressure.put(NOZZLE_MASS_FLOW_RATE, 0d);
        initialValuesChamberPressure.put(MASS_STORAGE_RATE, 0d);
        initialValuesChamberPressure.put(MASS_COMBUSTION_PRODUCTS, 0d);
        initialValuesChamberPressure.put(DENSITY_COMBUSTION_PRODUCTS, 0d);
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, 20, 1d,
                60d, 4d,
                INHIBITED, EXPOSED, EXPOSED);
        MotorChamber motorChamber = new MotorChamber(75d, 470d);

        double throatDiameter = 17.3985248919802;

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, motorChamber,
                6d, throatDiameter, 0d);

        Map<JSRMConstant, Double> constantsChamberPressure = Extract.extractConstants(solidRocketMotor);
        constantsChamberPressure.put(cstar, 889.279521360202);

        Map<Results, List<Double>> chamberPressureResults = new ChamberPressureCalculation(constantsChamberPressure, initialValuesChamberPressure).compute();

        chamberPressureProvider = new PerformanceResultProvider(chamberPressureMPA, chamberPressureResults.get(chamberPressureMPA));
        throatAreaProvider = new PerformanceResultProvider(throatArea, chamberPressureResults.get(throatArea));
        nozzleCriticalPassageAreaProvider = new PerformanceResultProvider(nozzleCriticalPassageArea, chamberPressureResults.get(nozzleCriticalPassageArea));
        timeSinceBurnStartProvider = new PerformanceResultProvider(timeSinceBurnStart, chamberPressureResults.get(timeSinceBurnStart));
    }
}