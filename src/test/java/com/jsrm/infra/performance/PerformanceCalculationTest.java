package com.jsrm.infra.performance;

import com.google.common.collect.ImmutableMap;
import com.jsrm.application.JSRMConfig;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.calculation.Formula;
import com.jsrm.infra.ConstantsExtractor;
import com.jsrm.infra.JSRMConstant;
import com.jsrm.infra.performance.csv.CsvToPerformanceLine;
import com.jsrm.infra.pressure.ChamberPressureCalculation;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.application.JSRMSimulationIT.createMotorAsSRM_2014ExcelFile;
import static com.jsrm.infra.JSRMConstant.*;
import static com.jsrm.infra.performance.PerformanceCalculation.Results.deliveredImpulse;
import static com.jsrm.infra.performance.PerformanceCalculation.Results.thrust;
import static com.jsrm.infra.performance.PerformanceFormulas.*;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
import static com.jsrm.infra.pressure.PressureFormulas.*;
import static com.jsrm.infra.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class PerformanceCalculationTest {

    private Map<PerformanceCalculation.Results, Offset<Double>> precisionByResults = ImmutableMap.<PerformanceCalculation.Results, Offset<Double>>builder()
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
                .put(at, 237.74683)
                .put(atfinal, 237.74683)
                .put(propellantId, ((double) KNDX.getId()))
                .build();

        Map<Formula, Double> initialValues = ImmutableMap.<Formula, Double>builder()
                .put(OPTIMUM_NOZZLE_EXPANSION_RATIO, 1.0)
                .put(DELIVERED_THRUST_COEFFICIENT, constants.get(etanoz))
                .put(THRUST, 0.0)
                .put(DELIVERED_IMPULSE, 0.0)
                .build();

        performanceResults = new PerformanceCalculation(constants, initialValues,
                chamberPressureProvider, throatAreaProvider,
                nozzleCriticalPassageAreaProvider, timeSinceBurnStartProvider)
                .compute(new JSRMConfig.Builder().withNozzleExpansionRatio(8).createJSRMConfig())
                .getResults();

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

        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();

        Map<JSRMConstant, Double> constantsChamberPressure = ConstantsExtractor.extract(solidRocketMotor, new JSRMConfig.Builder().createJSRMConfig(), KNDX.getId());

        Map<Results, List<Double>> chamberPressureResults = new ChamberPressureCalculation(constantsChamberPressure, initialValuesChamberPressure).compute();

        chamberPressureProvider = new PerformanceResultProvider(chamberPressureMPA, chamberPressureResults.get(chamberPressureMPA));
        throatAreaProvider = new PerformanceResultProvider(throatArea, chamberPressureResults.get(throatArea));
        nozzleCriticalPassageAreaProvider = new PerformanceResultProvider(nozzleCriticalPassageArea, chamberPressureResults.get(nozzleCriticalPassageArea));
        timeSinceBurnStartProvider = new PerformanceResultProvider(timeSinceBurnStart, chamberPressureResults.get(timeSinceBurnStart));
    }
}