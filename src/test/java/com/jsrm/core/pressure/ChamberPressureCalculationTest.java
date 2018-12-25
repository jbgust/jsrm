package com.jsrm.core.pressure;

import com.google.common.collect.ImmutableMap;
import com.jsrm.calculation.Formula;
import com.jsrm.core.pressure.csv.CsvToPressureLine;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;
import java.util.Map;

import static com.jsrm.core.pressure.ChamberPressureCalculation.*;
import static com.jsrm.core.pressure.csv.PressureCsvLineAggregator.LINE;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class ChamberPressureCalculationTest {

    private static Map<String, List<Double>> results;

    Map<String, Offset> precisionByFormulas = ImmutableMap.<String, Offset>builder()
            .put(throatArea, offset(0.1))
            .put(nozzleCriticalPassageArea, offset(0.00000001))
            .put(timeSinceBurnStart, offset(0.0001))
            .put(chamberPressureMPA, offset(0.001))
            .put(absoluteChamberPressure, offset(0.001))
            .put(absoluteChamberPressurePSIG, offset(0.1))
            .build();

    @BeforeAll
    static void init() throws Exception {

        Map<String, Double> constants = emptyMap();

        Map<Formula, Double> initialValues = emptyMap();


        ChamberPressureCalculation chamberPressureCalculation = new ChamberPressureCalculation();

        results = chamberPressureCalculation.compute();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_PRESSURE_SHEET_QUALIFICATION.csv", numLinesToSkip = 2, delimiter = '|')
    @DisplayName("Check pressure with SRM results")
    void qualification1(@CsvToPressureLine Map<String, Double> expectedLine) {
        int lineNumber = expectedLine.get(LINE).intValue();

        precisionByFormulas.forEach((formula, offset) ->
                assertThat(results.get(formula).get(lineNumber)).as(formula)
                        .isEqualTo(expectedLine.getOrDefault(formula,-111d), offset));
    }

}