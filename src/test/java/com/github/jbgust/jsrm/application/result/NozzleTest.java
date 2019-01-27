package com.github.jbgust.jsrm.application.result;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class NozzleTest {

    @Test
    void shouldReturnConvergenceLength(){
        Nozzle nozzle = new Nozzle(17.40, 75, 9.633, 54, 8, 49.2105, 2.3, 2.3);
        assertThat(nozzle.getConvergenceLenghtInMillimeter(35)).isEqualTo(41.13, offset(0.01));
    }

    @Test
    void shouldReturnDivergenceLength(){
        Nozzle nozzle = new Nozzle(17.40, 75, 9.633, 54, 8, 49.2105, 2.3, 2.3);
        assertThat(nozzle.getDivergenceLenghtInMillimeter(12)).isEqualTo(74.83, offset(0.01));
    }

    @Test
    void shouldReturnOptimalDivergenceLength(){
        Nozzle nozzle = new Nozzle(17.40, 75, 9.633, 54, 8, 49.2105, 2.3, 2.3);
        assertThat(nozzle.getOptimalDivergenceLenghtInMillimeter(12)).isEqualTo(86.10, offset(0.01));
    }

}