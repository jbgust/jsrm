package com.github.jbgust.jsrm.calculation;

import com.google.common.collect.ImmutableMap;
import com.github.jbgust.jsrm.application.exception.UnknownResultException;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.calculation.TestFormulas.FORMULA_1;
import static com.github.jbgust.jsrm.calculation.TestFormulas.FORMULA_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorResultsTest {

    @Test
    void shouldStoreResultOfGivenFormulas(){
        //GIVEN
        CalculatorResults results = new CalculatorResults(FORMULA_1);

        //WHEN
        results.addResult(ImmutableMap.of(FORMULA_1, 2d));
        results.addResult(ImmutableMap.of(FORMULA_1, 34d));

        //THEN
        assertThat(results.getResults(FORMULA_1))   .containsExactly(2d, 34d);
    }

    @Test
    void shouldThrowErrorIfResultIsNotStored(){
        //GIVEN
        CalculatorResults results = new CalculatorResults(FORMULA_1);

        //WHEN
        results.addResult(ImmutableMap.of(FORMULA_1, 2d));

        //THEN
        assertThatThrownBy(() -> results.getResult(FORMULA_3, 0))
                .isInstanceOf(UnknownResultException.class)
                .hasMessage("No result is stored for formula : FORMULA_3");

        assertThatThrownBy(() -> results.getResults(FORMULA_3))
                .isInstanceOf(UnknownResultException.class)
                .hasMessage("No result is stored for formula : FORMULA_3");
    }

}