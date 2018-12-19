package com.jsrm.core.pressure;

import com.jsrm.calculation.Calculator;
import com.jsrm.calculation.Formula;
import com.jsrm.core.pressure.csv.CsvToPressureLine;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.core.pressure.PressureFormulas.*;
import static com.jsrm.core.pressure.csv.PressureCsvLineAggregator.INTERVAL;
import static org.assertj.core.api.Assertions.assertThat;

class QualificationPressureCalculations {

    private static List<Map<Formula, Double>> results;

    @BeforeAll
    static void init(){
        HashMap<String, Double> constants = new HashMap<>();
        constants.put("ci",1d);
        constants.put("osi",0d);
        constants.put("ei",1d);
        double two = 24.5;
        constants.put("xincp", two /834d);
        constants.put("dc", 75d);
        constants.put("n", 4d);
        constants.put("vc", 2076396d);

        //initial throat diam
        constants.put("dto", 17.339d);
        constants.put("erate", 0d);

        //initial grain web thickness
        constants.put("two", two);

        constants.put("gstar", 6d);

        HashMap<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(GRAIN_CORE_DIAMETER, 20d);
        initialValues.put(GRAIN_OUTSIDE_DIAMETER, 69d);
        initialValues.put(GRAIN_LENGTH, 460d);

        Calculator calculator = new Calculator(GRAIN_VOLUME, constants, initialValues);
        results = calculator.compute(0, 835);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_QUALIFICATION.csv", numLinesToSkip = 1, delimiter = '|')
    @DisplayName("Check pressure with SRM results")
    void qualification1(@CsvToPressureLine Map<String, Double> expectedLine) {
        Map<Formula, Double> resultLIneToAssert = results.get(expectedLine.get(INTERVAL).intValue());


        assertThat(resultLIneToAssert.get(GRAIN_CORE_DIAMETER))
                .isEqualTo(expectedLine.get(GRAIN_CORE_DIAMETER.getName()), Offset.offset(0.01));

        assertThat(resultLIneToAssert.get(GRAIN_OUTSIDE_DIAMETER))
                .isEqualTo(expectedLine.get(GRAIN_OUTSIDE_DIAMETER.getName()), Offset.offset(0.01));

        assertThat(resultLIneToAssert.get(GRAIN_LENGTH))
                .isEqualTo(expectedLine.get(GRAIN_LENGTH.getName()), Offset.offset(0.1));
//
//        assertThat(resultLIneToAssert.get(WEB_THICKNESS))
//                .isEqualTo(expectedLine.get(WEB_THICKNESS.getName()), Offset.offset(0.001));
//
//        assertThat(resultLIneToAssert.get(THROAT_AREA))
//                .isEqualTo(expectedLine.get(THROAT_AREA.getName()), Offset.offset(0.1));
//
//        assertThat(resultLIneToAssert.get(NOZZLE_CRITICAL_PASSAGE_AREA))
//                .isEqualTo(expectedLine.get(NOZZLE_CRITICAL_PASSAGE_AREA.getName()), Offset.offset(0.00000001));

//        assertThat(resultLIneToAssert.get(EROSIVE_BURN_FACTOR))
//                .isEqualTo(expectedLine.get(EROSIVE_BURN_FACTOR.getName()), Offset.offset(0.01d));

        assertThat(resultLIneToAssert.get(GRAIN_VOLUME))
                .isEqualTo(expectedLine.get(GRAIN_VOLUME.getName()), Offset.offset(1d));
    }

}