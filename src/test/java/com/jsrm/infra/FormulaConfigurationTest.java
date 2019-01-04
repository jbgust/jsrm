package com.jsrm.infra;

import com.jsrm.infra.FormulaConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FormulaConfigurationTest {

    @Test
    void shouldThrowExceptionIfDependenciesAreNotUsedInFormula() {

        FormulaConfiguration formulaConfiguration = new FormulaConfiguration("new_formula_1_previous ^ 2");

        assertThatThrownBy(() -> formulaConfiguration.withDependencies("new_formula_1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("new_formula_1 is declared as dependency, but not used in the formula : new_formula_1_previous ^ 2");
    }

}