package com.jsrm.infra.pressure;

import com.google.common.collect.ImmutableMap;
import com.jsrm.application.JSRMConfig;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.calculation.Formula;
import com.jsrm.infra.ConstantsExtractor;
import com.jsrm.infra.JSRMConstant;
import com.jsrm.infra.pressure.csv.CsvToPressureLine;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsrm.application.JSRMSimulationIT.createMotorAsSRM_2014ExcelFile;
import static com.jsrm.infra.JSRMConstant.cstar;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results;
import static com.jsrm.infra.pressure.ChamberPressureCalculation.Results.*;
import static com.jsrm.infra.pressure.PressureFormulas.*;
import static com.jsrm.infra.pressure.csv.PressureCsvLineAggregator.LINE;
import static com.jsrm.infra.propellant.PropellantType.KNDX;
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

        Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(solidRocketMotor, new JSRMConfig.Builder().createJSRMConfig(), KNDX.getId());
        constants.put(cstar, 889.279521360202);

        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(GRAIN_CORE_DIAMETER, 20d);
        initialValues.put(GRAIN_OUTSIDE_DIAMETER, 69d);
        initialValues.put(GRAIN_LENGTH, 460d);

        //TODO
        initialValues.put(TIME_SINCE_BURN_STARTS, 0d);
        initialValues.put(TEMPORARY_CHAMBER_PRESSURE, 0.101);
        initialValues.put(MASS_GENERATION_RATE, 0d);
        initialValues.put(NOZZLE_MASS_FLOW_RATE, 0d);
        initialValues.put(MASS_STORAGE_RATE, 0d);
        initialValues.put(MASS_COMBUSTION_PRODUCTS, 0d);
        initialValues.put(DENSITY_COMBUSTION_PRODUCTS, 0d);



        ChamberPressureCalculation chamberPressureCalculation = new ChamberPressureCalculation(constants, initialValues);

        results = chamberPressureCalculation.compute();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_PRESSURE_SHEET_QUALIFICATION.csv", numLinesToSkip = 2, delimiter = '|')
    @DisplayName("Check pressure with SRM results")
    void qualification1(@CsvToPressureLine Map<String, Double> expectedLine) {
        int lineNumber = expectedLine.get(LINE).intValue();

        precisionByResults.forEach((result, offset) ->
                assertThat(results.get(result).get(lineNumber)).as(result.name())
                        .isEqualTo(expectedLine.getOrDefault(result.name(),-111d), offset));
    }

}