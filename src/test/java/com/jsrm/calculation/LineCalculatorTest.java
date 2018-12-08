package com.jsrm.calculation;

import com.jsrm.calculation.formulas.Formula1;
import com.jsrm.calculation.formulas.Formula2;
import com.jsrm.calculation.formulas.Formula3;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
        Formula3 formula = new Formula3();
        Map<String, Double> initialValues = new HashMap<>();
        initialValues.put(formula.getName(), 25d);
        Map<String, Double> constants = emptyMap();
        LineCalculator lineCalculator = new LineCalculator(formula, constants, initialValues);

        // WHEN
        Map<String, Double> results = lineCalculator.compute(0);

        // THEN
        assertThat(results).containsExactly(entry(formula.getName(), 25d));
    }

    @Test
    void shouldUsePreviousValue() {
        // GIVEN
        Formula3 formula = new Formula3();

        Map<String, Double> initialValues = new HashMap<>();
        initialValues.put(formula.getName(), 25d);

        Map<String, Double> constants = emptyMap();

        LineCalculator lineCalculator = new LineCalculator(formula, constants, initialValues);
        lineCalculator.compute(0);

        // WHEN
        Map<String, Double> results = lineCalculator.compute(1);

        // THEN
        assertThat(results).containsExactly(entry(formula.getName(), 100d));
    }

    @Test
    void shouldUseConstant(){
        // GIVEN
        Formula formula = new Formula2();

        Map<String, Double> initialValues = new HashMap<>();
        initialValues.put("formula3", 2d);

        Map<String, Double> constants = new HashMap<>();
        constants.put("e", 4d);

        LineCalculator lineCalculator = new LineCalculator(formula, constants, initialValues);

        // WHEN
        Map<String, Double> results = lineCalculator.compute(1);

        // THEN
        assertThat(results).containsExactly(
                entry(formula.getName(), 32d),
                entry("formula3", 2d));

    }

    @Test
    void shouldComputeDependencies(){
        // GIVEN
        Formula formula = new Formula1();
        Map<String, Double> constants = new HashMap<>();
        constants.put("constant1", 4d);
        constants.put("e", 2d);

        Map<String, Double> initialValues = new HashMap<>();
        initialValues.put("formula3", 3d);
        LineCalculator lineCalculator = new LineCalculator(formula, constants, initialValues);

        // WHEN
        Map<String, Double> result1 = lineCalculator.compute(0);
        Map<String, Double> result2 = lineCalculator.compute(1);

        // THEN
        assertThat(result1).containsExactly(
                entry("formula1", -2d),
                entry("formula2", 12d),
                entry("formula3", 3d));

        assertThat(result2).containsExactly(
                entry("formula1", -11d),
                entry("formula2", 48d),
                entry("formula3", 12d));
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

    private Formula mockFormula(String formulaName) {
        Formula formula = mock(Formula.class);
        given(formula.getName()).willReturn(formulaName);
        given(formula.getExpression()).willReturn(new ExpressionBuilder("4 + 2").build());
        return formula;
    }
}