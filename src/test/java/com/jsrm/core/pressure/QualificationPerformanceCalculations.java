package com.jsrm.core.pressure;

import com.google.common.collect.ImmutableMap;
import com.jsrm.calculation.CalculatorResults;
import com.jsrm.calculation.Formula;
import com.jsrm.calculation.LineCalculator;
import com.jsrm.calculation.ResultLineProvider;
import com.jsrm.core.pressure.csv.CsvToPerformanceLine;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static com.jsrm.core.JSRMConstant.*;
import static com.jsrm.core.performance.PerformanceFormulas.*;
import static com.jsrm.core.pressure.ChamberPressureCalculation.*;
import static com.jsrm.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class QualificationPerformanceCalculations {

    private static CalculatorResults results;
    private static LineCalculator lineCalculator2;

    Map<Formula, Offset> precisionByFormulas = ImmutableMap.<Formula, Offset>builder()
            .put(CHAMBER_PRESSUER_PA, offset(1.0))
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
        chamberPressureProvider = new TestResultProvider(chamberPressureMPA);
        throatAreaProvider = new TestResultProvider(throatArea);
        nozzleCriticalPassageAreaProvider = new TestResultProvider(nozzleCriticalPassageArea);
        timeSinceBurnStartProvider = new TestResultProvider(timeSinceBurnStart);;

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
    @DisplayName("Check performance with SRM results")
    void qualification(@CsvToPerformanceLine Map<String, Double> performanceLine) {
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

        line++;
    }

    private static class TestResultProvider implements ResultLineProvider {

        private final String name;
        private Map<String, Double> csvData;

        private TestResultProvider(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public double getResult(int lineNumber) {
            return csvData.get(name);
        }

        public void setCsvData(Map<String, Double> csvData){
            this.csvData = csvData;
        }

    }
}