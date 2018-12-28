package com.jsrm.core.pressure;

import com.google.common.collect.ImmutableMap;
import com.jsrm.calculation.Formula;
import com.jsrm.core.JSRMConstant;
import com.jsrm.core.pressure.csv.CsvToPressureLine;
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

import static com.jsrm.core.JSRMConstant.cstar;
import static com.jsrm.core.pressure.ChamberPressureCalculation.*;
import static com.jsrm.core.pressure.ChamberPressureCalculation.Results.*;
import static com.jsrm.core.pressure.PressureFormulas.*;
import static com.jsrm.core.pressure.csv.PressureCsvLineAggregator.LINE;
import static com.jsrm.motor.GrainSurface.EXPOSED;
import static com.jsrm.motor.GrainSurface.INHIBITED;
import static com.jsrm.motor.propellant.PropellantType.KNDX;
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

        PropellantGrain propellantGrain = new PropellantGrain(KNDX, 20, 1d,
                60d, 4d,
                INHIBITED, EXPOSED, EXPOSED);
        MotorChamber motorChamber = new MotorChamber(75d, 470d);

        double throatDiameter = 17.3985248919802;

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, motorChamber,
                6d, throatDiameter, 0d);

        Map<JSRMConstant, Double> constants = Extract.extractConstants(solidRocketMotor);
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