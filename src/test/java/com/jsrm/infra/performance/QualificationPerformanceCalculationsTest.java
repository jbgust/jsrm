package com.jsrm.infra.performance;

import com.google.common.collect.ImmutableMap;
import com.jsrm.calculation.Formula;
import com.jsrm.calculation.LineCalculator;
import com.jsrm.infra.performance.csv.CsvToPerformanceLine;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static com.jsrm.infra.JSRMConstant.*;
import static com.jsrm.infra.performance.PerformanceFormulas.*;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
import static com.jsrm.application.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class QualificationPerformanceCalculationsTest {

    private static LineCalculator lineCalculator2;

    private Map<Formula, Offset<Double>> precisionByFormulas = ImmutableMap.<Formula, Offset<Double>>builder()
            .put(CHAMBER_PRESSURE_PA, offset(1.0))
            .put(NOZZLE_EXPANSION_RATIO, offset(0.001))
            .put(NOZZLE_EXIT_PRESSURE, offset(1.0))
            .put(OPTIMUM_NOZZLE_EXPANSION_RATIO, offset(0.0001))
            .put(DELIVERED_THRUST_COEFFICIENT, offset(0.001))
            .put(THRUST, offset(1.0))
            .put(DELIVERED_IMPULSE, offset(0.001))
            .put(MACH_SPEED_AT_NOZZLE_EXIT, offset(0.001))
            .build();

    private static TestResultProvider chamberPressureProvider;
    private static TestResultProvider throatAreaProvider;
    private static TestResultProvider nozzleCriticalPassageAreaProvider;
    private static TestResultProvider timeSinceBurnStartProvider;
    private static LineCalculator lineCalculator1;

    private static int line;


    @BeforeAll
    static void init(){
        chamberPressureProvider = new TestResultProvider(chamberPressureMPA.name());
        throatAreaProvider = new TestResultProvider(throatArea.name());
        nozzleCriticalPassageAreaProvider = new TestResultProvider(nozzleCriticalPassageArea.name());
        timeSinceBurnStartProvider = new TestResultProvider(timeSinceBurnStart.name());

        Map<String, Double> constants = ImmutableMap.<String, Double>builder()
                .put(patm.name(), 0.101)
                .put(etanoz.name(), 0.85)
                .put(k2ph.name(), KNDX.getK2Ph())
                .put(aexit.name(), 1901.974657752680)
                .put(me.name(), 2.95455756202289)
                .put(mef.name(), 2.95455756202289)
                .build();

        Map<Formula, Double> initialValues = ImmutableMap.<Formula, Double>builder()
                .put(OPTIMUM_NOZZLE_EXPANSION_RATIO, 1.0)
                .put(DELIVERED_THRUST_COEFFICIENT, constants.get(etanoz.name()))
                .put(THRUST, 0.0)
                .put(DELIVERED_IMPULSE, 0.0)
                .put(MACH_SPEED_AT_NOZZLE_EXIT, constants.get(me.name()))
                .build();

        lineCalculator1 = new LineCalculator(DELIVERED_IMPULSE, constants, initialValues,
                newHashSet(chamberPressureProvider, throatAreaProvider, nozzleCriticalPassageAreaProvider, timeSinceBurnStartProvider));

        lineCalculator2 = new LineCalculator(OPTIMUM_NOZZLE_EXPANSION_RATIO, constants, initialValues,
                newHashSet(chamberPressureProvider, throatAreaProvider, nozzleCriticalPassageAreaProvider, timeSinceBurnStartProvider));

        line = 0;
    }



    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_PERFORMANCE_QUALIFICATION.csv", numLinesToSkip = 2, delimiter = '|')
    @DisplayName("Qualify performance with SRM results")
    void qualification(@CsvToPerformanceLine Map<String, Double> performanceLine) {

        //The last line should be manually computed, so not tested here but in PerformanceCalculation
        if(line < 882) {
            chamberPressureProvider.setCsvData(performanceLine);
            throatAreaProvider.setCsvData(performanceLine);
            nozzleCriticalPassageAreaProvider.setCsvData(performanceLine);
            timeSinceBurnStartProvider.setCsvData(performanceLine);

            Map<Formula, Double> lineResult = lineCalculator1.compute(line);
            lineResult.put(OPTIMUM_NOZZLE_EXPANSION_RATIO, lineCalculator2.compute(line).get(OPTIMUM_NOZZLE_EXPANSION_RATIO));

            precisionByFormulas.forEach((formula, offset) ->
                    assertThat(lineResult.get(formula))
                            .as(formula.getName())
                            .isEqualTo(performanceLine.getOrDefault(formula.getName(), -111.0), offset));
        }

        line++;
    }

}