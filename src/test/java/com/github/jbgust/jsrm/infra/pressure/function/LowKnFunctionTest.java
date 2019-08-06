package com.github.jbgust.jsrm.infra.pressure.function;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LowKnFunctionTest {

    @Test
    void shouldNotChangeMassStorageRateResult() {
        LowKnFunction lowKnFunction = new LowKnFunction();
        int useSafeKN = 0;

        assertThat(lowKnFunction.runFunction(15.23, useSafeKN)).isEqualTo(15.23);
    }

    @Test
    void shouldNotChangeMassStorageRateResultWhenMassStorageRateResultIsGreaterOrEquals0() {
        LowKnFunction lowKnFunction = new LowKnFunction();
        int useSafeKN = 1;

        assertThat(lowKnFunction.runFunction(0.0, useSafeKN)).isEqualTo(0.0);
        assertThat(lowKnFunction.runFunction(15.23, useSafeKN)).isEqualTo(15.23);
    }

    @Test
    void shouldChangeMassStorageRateResultWhenMassStorageRateResultIsLessThan0() {
        LowKnFunction lowKnFunction = new LowKnFunction();
        int useSafeKN = 1;

        assertThat(lowKnFunction.runFunction(-0.1, useSafeKN)).isEqualTo(0.0001);
    }

}