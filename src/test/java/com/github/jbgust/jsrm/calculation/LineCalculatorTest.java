package com.github.jbgust.jsrm.calculation;

import net.objecthunter.exp4j.ExpressionBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static com.github.jbgust.jsrm.calculation.TestFormulas.*;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.collections.Sets.newSet;

class LineCalculatorTest {

    @Test
    void shouldUseInitialValue() {
        // GIVEN
        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(FORMULA_3, 25d);
        Map<String, Double> constants = emptyMap();
        LineCalculator lineCalculator = new LineCalculator(FORMULA_3, constants, initialValues);

        // WHEN
        Map<Formula, Double> results = lineCalculator.compute(0);

        // THEN
        Assertions.assertThat(results).containsExactly(entry(FORMULA_3, 25d));
    }

    @Test
    void shouldUsePreviousValue() {
        // GIVEN
        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(FORMULA_3, 25d);

        Map<String, Double> constants = emptyMap();

        LineCalculator lineCalculator = new LineCalculator(FORMULA_3, constants, initialValues);
        lineCalculator.compute(0);

        // WHEN
        Map<Formula, Double> results = lineCalculator.compute(1);

        // THEN
        Assertions.assertThat(results).containsExactly(entry(FORMULA_3, 100d));
    }

    @Test
    void shouldUseConstant(){
        // GIVEN
        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(FORMULA_3, 2d);

        Map<String, Double> constants = new HashMap<>();
        constants.put("e", 4d);

        LineCalculator lineCalculator = new LineCalculator(FORMULA_2, constants, initialValues);

        // WHEN
        Map<Formula, Double> results = lineCalculator.compute(1);

        // THEN
        Assertions.assertThat(results)
                .containsOnly(
                    entry(FORMULA_2, 32d),
                    entry(FORMULA_3, 2d))
                .hasSize(2);
    }

    @Test
    void shouldResolveDependenciesWhenInitialValuesAreUsed() {
        //GIVEN
        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(FORMULA_5, 2d);
        LineCalculator lineCalculator = new LineCalculator(FORMULA_5, emptyMap(), initialValues);

        //WHEN
        Map<Formula, Double> results = lineCalculator.compute(0);

        //THEN
        assertThat(results.get(FORMULA_6)).isEqualTo(3d);
    }

    @Test
    void shouldUseResultLineProviderAsPreviousValue() {
        //GIVEN
        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(FORMULA_7, 2d);
        ResultLineProvider resultLineProvider = new ResultLineProvider() {
            @Override
            public String getName() {
                return "PROVIDED_DATA";
            }

            @Override
            public double getResult(int lineNumber) {
                return 8;
            }
        };

        LineCalculator lineCalculator = new LineCalculator(FORMULA_7, emptyMap(), initialValues, newHashSet(resultLineProvider));
        lineCalculator.compute(0);

        //WHEN
        Map<Formula, Double> results = lineCalculator.compute(1);

        //THEN
        assertThat(results.get(FORMULA_7)).isEqualTo(32d);
    }

    @Test
    void shouldComputeDependencies(){
        // GIVEN
        Map<String, Double> constants = new HashMap<>();
        constants.put("constant1", 4d);
        constants.put("e", 2d);

        Map<Formula, Double> initialValues = new HashMap<>();
        initialValues.put(FORMULA_3, 3d);
        LineCalculator lineCalculator = new LineCalculator(FORMULA_1, constants, initialValues);

        // WHEN
        Map<Formula, Double> result1 = lineCalculator.compute(0);
        Map<Formula, Double> result2 = lineCalculator.compute(1);

        // THEN
        Assertions.assertThat(result1)
                .containsOnly(
                        entry(FORMULA_1, -2d),
                        entry(FORMULA_2, 12d),
                        entry(FORMULA_3, 3d))
                .hasSize(3);

        Assertions.assertThat(result2)
                .containsOnly(
                    entry(FORMULA_1, -11d),
                    entry(FORMULA_2, 48d),
                    entry(FORMULA_3, 12d))
                .hasSize(3);
    }

    @Test
    void shouldNotRecomputeADependency(){
        // GIVEN
        Formula formula1 = mockFormula("f1");
        Formula formula2 = mockFormula("f2");
        Formula formula3 = mockFormula("f3");
        Formula formula4 = mockFormula("f4");

        given(formula1.getDependencies()).willReturn(newSet(formula2, formula3));
        given(formula2.getDependencies()).willReturn(newSet(formula4));
        given(formula3.getDependencies()).willReturn(newSet(formula4));
        given(formula4.getDependencies()).willReturn(emptySet());

        LineCalculator lineCalculator = new LineCalculator(formula1, emptyMap(), emptyMap());

        // WHEN
        lineCalculator.compute(0);

        //THEN
        verify(formula4, times(1)).getExpression();
    }

    @RepeatedTest(5)
    void shouldUseFunctionInFormula(RepetitionInfo repetitionInfo) {
        // GIVEN
        ResultLineProvider resultLineProvider = new ResultLineProvider() {
            @Override
            public String getName() {
                return "FUNCTION_A";
            }

            @Override
            public double getResult(int lineNumber) {
                return lineNumber+2;
            }
        };

        LineCalculator lineCalculator = new LineCalculator(FORMULA_4, emptyMap(), emptyMap(), newSet(resultLineProvider));

        // WHEN
        Map<Formula, Double> result = lineCalculator.compute(repetitionInfo.getCurrentRepetition());

        //THEN
        assertThat(result.get(FORMULA_4)).isEqualTo(2*(repetitionInfo.getCurrentRepetition()+2));
    }

    private Formula mockFormula(String formulaName) {
        Formula formula = mock(Formula.class);
        given(formula.getName()).willReturn(formulaName);
        given(formula.getExpression()).willReturn(new ExpressionBuilder("4 + 2").build());
        return formula;
    }
}