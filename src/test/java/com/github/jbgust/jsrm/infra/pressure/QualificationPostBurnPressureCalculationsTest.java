package com.github.jbgust.jsrm.infra.pressure;

import com.github.jbgust.jsrm.calculation.Calculator;
import com.github.jbgust.jsrm.calculation.CalculatorBuilder;
import com.github.jbgust.jsrm.calculation.CalculatorResults;
import com.github.jbgust.jsrm.calculation.Formula;
import com.github.jbgust.jsrm.infra.JSRMConstant;
import com.github.jbgust.jsrm.infra.pressure.csv.CsvToPostBurnPressureLine;
import com.github.jbgust.jsrm.infra.pressure.csv.PostBurnPressureCsvLineAggregator;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class QualificationPostBurnPressureCalculationsTest {

    private static CalculatorResults results;

    private Map<Formula, Offset<Double>> precisionByFormulas = ImmutableMap.<Formula, Offset<Double>>builder()
            .put(PostBurnPressureFormulas.POST_BURN_TIME_SINCE_BURN_STARTS, offset(0.0001))
            .put(PostBurnPressureFormulas.POST_BURN_CHAMBER_PRESSURE_MPA, offset(0.001))
            .put(PostBurnPressureFormulas.POST_BURN_ABSOLUTE_CHAMBER_PRESSURE, offset(0.001))
            .put(PostBurnPressureFormulas.POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG, offset(0.1))
            .build();

    @BeforeAll
    static void init() {

        Map<String, Double> variablesTbinc = ImmutableMap.<String, Double>builder()
                .put(JSRMConstant.vc.name(), 2076396.394482)
                .put(JSRMConstant.expectedPfinal.name(), 0.203044747800798)
                .put(JSRMConstant.pbout.name(), 3.89641961658439)
                .put(JSRMConstant.rat.name(), 196.131163010144)
                .put(JSRMConstant.to.name(), 1624.5)
                .put(JSRMConstant.astarf.name(), 0.000237746832219086)
                .put(JSRMConstant.cstar.name(), 889.279521360202)
                .put("nbLine", 47d)
                .build();
        IncrementTimeBurstSolver incrementTimeBurstSolver = new IncrementTimeBurstSolver();
        double tbincValue = incrementTimeBurstSolver.solve(variablesTbinc);

        Map<String, Double> constants = ImmutableMap.<String, Double>builder()
                .put(JSRMConstant.tbinc.name(), tbincValue)
                .put(JSRMConstant.tbout.name(), 2.08552640517936)
                .put(JSRMConstant.patm.name(), 0.101)
                .putAll(variablesTbinc)
                .build();

        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(PostBurnPressureFormulas.POST_BURN_TIME_SINCE_BURN_STARTS, constants.get(JSRMConstant.tbout.name())+tbincValue);


        Calculator calculator = new CalculatorBuilder(PostBurnPressureFormulas.POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withResultsToSave(PostBurnPressureFormulas.values())
                .withConstants(constants)
                .withInitialValues(initialValues)
                .createCalculator();

        results = calculator.compute(0, 47);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_POST_BURN_PRESSURE_QUALIFICATION.csv", numLinesToSkip = 1, delimiter = '|')
    @DisplayName("Qualify post burn pressure with SRM results")
    void qualification(@CsvToPostBurnPressureLine Map<String, Double> expectedLine) {
        int lineNumber = expectedLine.get(PostBurnPressureCsvLineAggregator.LINE).intValue();

        // line 48 should not be computed here
        if(lineNumber < 47) {
            precisionByFormulas.forEach((formula, offset) ->
                    assertThat(results.getResult(formula, lineNumber)).as(formula.getName())
                            .isEqualTo(expectedLine.getOrDefault(formula.getName(), -111d), offset));
        }

    }
}