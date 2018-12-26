package com.jsrm.core.pressure;

import com.google.common.collect.ImmutableMap;
import com.jsrm.calculation.Calculator;
import com.jsrm.calculation.CalculatorBuilder;
import com.jsrm.calculation.CalculatorResults;
import com.jsrm.calculation.Formula;
import com.jsrm.core.pressure.csv.CsvToPostBurnPressureLine;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.core.JSRMConstant.*;
import static com.jsrm.core.pressure.PostBurnPressureFormulas.*;
import static com.jsrm.core.pressure.csv.PostBurnPressureCsvLineAggregator.LINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class QualificationPostBurnPressureCalculations {

    private static CalculatorResults results;

    Map<Formula, Offset> precisionByFormulas = ImmutableMap.<Formula, Offset>builder()
            .put(POST_BURN_TIME_SINCE_BURN_STARTS, offset(0.0001))
            .put(POST_BURN_CHAMBER_PRESSURE_MPA, offset(0.001))
            .put(POST_BURN_ABSOLUTE_CHAMBER_PRESSURE, offset(0.001))
            .put(POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG, offset(0.1))
            .build();

    @BeforeAll
    static void init() {

        Map<String, Double> variablesTbinc = ImmutableMap.<String, Double>builder()
                .put(vc.name(), 2076396.394482)
                .put(expectedPfinal.name(), 0.203044747800798)
                .put(pbout.name(), 3.89641961658439)
                .put(rat.name(), 196.131163010144)
                .put(to.name(), 1624.5)
                .put(astarf.name(), 0.000237746832219086)
                .put(cstar.name(), 889.279521360202)
                .put("nbLine", 47d)
                .build();
        IncrementTimeBurstSolver incrementTimeBurstSolver = new IncrementTimeBurstSolver();
        double tbincValue = incrementTimeBurstSolver.solve(variablesTbinc);

        Map<String, Double> constants = ImmutableMap.<String, Double>builder()
                .put(tbinc.name(), tbincValue)
                .put(tbout.name(), 2.08552640517936)
                .put(patm.name(), 0.101)
                .putAll(variablesTbinc)
                .build();

        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(POST_BURN_TIME_SINCE_BURN_STARTS, constants.get(tbout.name())+tbincValue);


        Calculator calculator = new CalculatorBuilder(POST_BURN_ABSOLUTE_CHAMBER_PRESSURE_PSIG)
                .withResultsToSave(PostBurnPressureFormulas.values())
                .withConstants(constants)
                .withInitialValues(initialValues)
                .createCalculator();

        results = calculator.compute(0, 47);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_POST_BURN_PRESSURE_QUALIFICATION.csv", numLinesToSkip = 1, delimiter = '|')
    @DisplayName("Check post burn pressure with SRM results")
    void qualification(@CsvToPostBurnPressureLine Map<String, Double> expectedLine) {
        int lineNumber = expectedLine.get(LINE).intValue();

        // line 48 should not be computed here
        if(lineNumber < 47) {
            precisionByFormulas.forEach((formula, offset) -> {
                assertThat(results.getResult(formula, lineNumber)).as(formula.getName())
                        .isEqualTo(expectedLine.getOrDefault(formula.getName(), -111d), offset);
            });
        }

    }
}