package com.jsrm.motor.formula;

import org.junit.jupiter.api.Test;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThroatAreaTest {

    @Test
    void shouldComputeThroatErosionWithoutErosion() {
        // THEN
        assertThat(ThroatArea.compute(10, 0, 0)).isEqualTo(PI * pow(5, 2), offset(0.0001));
    }

    @Test
    void shouldComputeThroatErosionWithErosion() {
        // THEN
        assertThat(ThroatArea.compute(10, 2, 0.5)).isEqualTo(PI * pow(5.5, 2), offset(0.0001));
    }

    @Test
    void shouldFailInBurnProgressionIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> ThroatArea.compute(10, 2, 1.2), "burnProgresion should be in this range [0;1]");
    }
}