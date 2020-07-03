package com.github.jbgust.jsrm.infra.pressure.function;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class CoreMachNumberFunctionTest {

    @Test
    void shouldReturnCoreMachNumber() {
        CoreMachNumberFunction coreMachNumberFunction = new CoreMachNumberFunction();

        assertThat(coreMachNumberFunction.runFunction(3d, 1.2)).isCloseTo(0.2, offset(0.01));
    }

}
