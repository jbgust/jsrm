package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.calculation.Calculator;
import com.github.jbgust.jsrm.calculation.CalculatorBuilder;
import com.github.jbgust.jsrm.calculation.CalculatorResults;
import com.github.jbgust.jsrm.calculation.Formula;
import com.github.jbgust.jsrm.infra.ConstantsExtractor;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import com.github.jbgust.jsrm.infra.pressure.csv.CsvToDuringBurnPressureLine;
import com.github.jbgust.jsrm.infra.pressure.csv.DuringBurnPressureCsvLineAggregator;
import com.github.jbgust.jsrm.infra.pressure.resultprovider.EndGrainSurfaceResultProvider;
import com.github.jbgust.jsrm.infra.pressure.resultprovider.GrainVolumeResultProvider;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.Map;

import static com.github.jbgust.jsrm.infra.pressure.PressureFormulas.*;
import static com.github.jbgust.jsrm.utils.SolidRocketMotorBuilder.createMotorAsSRM_2014ExcelFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class QualificationDuringBurnPressureCalculationsTest {

    private static CalculatorResults results;

    private Map<Formula, Offset<Double>> precisionByFormulas = ImmutableMap.<Formula, Offset<Double>>builder()
            .put(THROAT_AREA, offset(0.1))
            .put(NOZZLE_CRITICAL_PASSAGE_AREA, offset(0.00000001))
            .put(EROSIVE_BURN_FACTOR, offset(0.01))
            .put(TEMPORARY_CHAMBER_PRESSURE, offset(0.001))
            .put(PROPELLANT_BURN_RATE, offset(0.001))
            .put(TIME_SINCE_BURN_STARTS, offset(0.0001))
            .put(AI, offset(0.0001))
            .put(MASS_GENERATION_RATE, offset(0.0001))
            .put(NOZZLE_MASS_FLOW_RATE, offset(0.0001))
            .put(MASS_STORAGE_RATE, offset(0.0001))
            .put(MASS_COMBUSTION_PRODUCTS, offset(0.0001))
            .put(DENSITY_COMBUSTION_PRODUCTS, offset(0.001))
            .put(CHAMBER_PRESSURE_MPA, offset(0.001))
            .put(ABSOLUTE_CHAMBER_PRESSURE, offset(0.001))
            .put(ABSOLUTE_CHAMBER_PRESSURE_PSIG, offset(0.1))
            .build();

    @BeforeAll
    static void init(){
        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();

        JSRMConfig jsrmConfig = new JSRMConfigBuilder().createJSRMConfig();
        Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(solidRocketMotor, jsrmConfig);

        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(TIME_SINCE_BURN_STARTS, 0d);
        initialValues.put(TEMPORARY_CHAMBER_PRESSURE, 0.101);
        initialValues.put(MASS_GENERATION_RATE, 0d);
        initialValues.put(NOZZLE_MASS_FLOW_RATE, 0d);
        initialValues.put(MASS_STORAGE_RATE, 0d);
        initialValues.put(MASS_COMBUSTION_PRODUCTS, 0d);
        initialValues.put(DENSITY_COMBUSTION_PRODUCTS, 0d);

        Calculator calculator = new CalculatorBuilder(ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withResultsToSave(PressureFormulas.values())
                .withConstants(ConstantsExtractor.toCalculationFormat(constants))
                .withInitialValues(initialValues)
                .withResultLineProviders(
                        new EndGrainSurfaceResultProvider(solidRocketMotor.getPropellantGrain().getGrainConfigutation(),jsrmConfig.getNumberLineDuringBurnCalculation()),
                        new GrainVolumeResultProvider(solidRocketMotor.getPropellantGrain().getGrainConfigutation(),jsrmConfig.getNumberLineDuringBurnCalculation()))
                .createCalculator();

        results = calculator.compute(0, 835);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_DURING_BURN_PRESSURE_QUALIFICATION.csv", numLinesToSkip = 1, delimiter = '|')
    @DisplayName("Qualify pressure (during propellant burn) with SRM results")
    void qualification(@CsvToDuringBurnPressureLine Map<String, Double> expectedLine) {
        int lineNumber = expectedLine.get(DuringBurnPressureCsvLineAggregator.INTERVAL).intValue();

        precisionByFormulas.forEach((formula, offset) ->
                assertThat(results.getResult(formula, lineNumber)).as(formula.getName())
                .isEqualTo(expectedLine.getOrDefault(formula.getName(),-111d), offset));
    }
}
