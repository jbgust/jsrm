package com.jsrm.pressure;

import com.jsrm.calculation.Calculator;
import com.jsrm.pressure.csv.CsvToPressureLine;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.pressure.PressureFormulas.CORE_DIAMETER;
import static com.jsrm.pressure.csv.PressureCsvLineAggregator.INTERVAL;
import static org.assertj.core.api.Assertions.assertThat;

class QualificationPressureCalculations {

    private static List<Map<String, Double>> results;

    @BeforeAll
    static void init(){
        HashMap<String, Double> constants = new HashMap<>();
        constants.put("ci",1d);
        constants.put("xincp", 6.6/834d);

        HashMap<String, Double> initialValues = new HashMap<>();
        initialValues.put(CORE_DIAMETER.name(),8d);

        Calculator calculator = new Calculator(CORE_DIAMETER, constants, initialValues);
        results = calculator.compute(0, 835);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/METEOR-KNSB-final_E46_QUALIFICATION.csv", numLinesToSkip = 1, delimiter = '|')
    @DisplayName("Check pressure data for METEOR motor with SRM results")
    void qualification1(@CsvToPressureLine Map<String, Double> expectedLine) {
        assertThat(results.get(expectedLine.get(INTERVAL).intValue()).get(CORE_DIAMETER.getName()))
                .isEqualTo(expectedLine.get(CORE_DIAMETER.getName()), Offset.offset(0.01));
    }



}