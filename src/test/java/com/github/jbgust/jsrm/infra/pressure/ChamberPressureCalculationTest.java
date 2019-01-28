package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.infra.ConstantsExtractor;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import com.github.jbgust.jsrm.infra.pressure.csv.CsvToPressureLine;
import com.github.jbgust.jsrm.infra.pressure.csv.PressureCsvLineAggregator;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;
import java.util.Map;

import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results;
import static com.github.jbgust.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
import static com.github.jbgust.jsrm.utils.SolidRocketMotorBuilder.createMotorAsSRM_2014ExcelFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class ChamberPressureCalculationTest {

    private static Map<Results, List<Double>> results;

    private Map<Results, Offset<Double>> precisionByResults = ImmutableMap.<Results, Offset<Double>>builder()
            .put(throatArea, offset(0.1))
            .put(nozzleCriticalPassageArea, offset(0.00000001))
            .put(timeSinceBurnStart, offset(0.0001))
            .put(chamberPressureMPA, offset(0.001))
            .put(absoluteChamberPressure, offset(0.001))
            .put(absoluteChamberPressurePSIG, offset(0.1))
            .build();

    @BeforeAll
    static void init() {
        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();
        JSRMConfig config = new JSRMConfigBuilder().createJSRMConfig();

        Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(solidRocketMotor, config);

        ChamberPressureCalculation chamberPressureCalculation = new ChamberPressureCalculation(solidRocketMotor, config, constants);

        results = chamberPressureCalculation.compute();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_PRESSURE_SHEET_QUALIFICATION.csv", numLinesToSkip = 2, delimiter = '|')
    @DisplayName("Check pressure with SRM results")
    void qualification1(@CsvToPressureLine Map<String, Double> expectedLine) {
        int lineNumber = expectedLine.get(PressureCsvLineAggregator.LINE).intValue();

        precisionByResults.forEach((result, offset) ->
                assertThat(results.get(result).get(lineNumber)).as(result.name())
                        .isEqualTo(expectedLine.getOrDefault(result.name(),-111d), offset));
    }

}