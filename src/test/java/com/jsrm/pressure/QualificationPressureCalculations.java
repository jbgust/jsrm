package com.jsrm.pressure;

import com.jsrm.calculation.Calculator;
import com.jsrm.calculation.Formula;
import com.jsrm.pressure.csv.CsvToPressureLine;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.pressure.PressureFormulas.*;
import static com.jsrm.pressure.csv.PressureCsvLineAggregator.INTERVAL;
import static org.assertj.core.api.Assertions.assertThat;

class QualificationPressureCalculations {

    private static List<Map<Formula, Double>> results;

    @BeforeAll
    static void init(){
        HashMap<String, Double> constants = new HashMap<>();
        constants.put("ci",1d);
        constants.put("osi",0d);
        constants.put("xincp", 6.6/834d);

        HashMap<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(GRAIN_CORE_DIAMETER,8d);
        initialValues.put(GRAIN_OUTSIDE_DIAMETER,21.2d);

        Calculator calculator = new Calculator(WEB_THICKNESS, constants, initialValues);
        results = calculator.compute(0, 835);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/METEOR-KNSB-final_E46_QUALIFICATION.csv", numLinesToSkip = 1, delimiter = '|')
    @DisplayName("Check pressure data for METEOR motor with SRM results")
    void qualification1(@CsvToPressureLine Map<String, Double> expectedLine) {
        Map<Formula, Double> resultLIneToAssert = results.get(expectedLine.get(INTERVAL).intValue());

        assertThat(resultLIneToAssert.get(GRAIN_CORE_DIAMETER))
                .isEqualTo(expectedLine.get(GRAIN_CORE_DIAMETER.getName()), Offset.offset(0.01));

        assertThat(resultLIneToAssert.get(GRAIN_OUTSIDE_DIAMETER))
                .isEqualTo(expectedLine.get(GRAIN_OUTSIDE_DIAMETER.getName()), Offset.offset(0.01));

        assertThat(resultLIneToAssert.get(WEB_THICKNESS))
                .isEqualTo(expectedLine.get(WEB_THICKNESS.getName()), Offset.offset(0.001));;
    }



}