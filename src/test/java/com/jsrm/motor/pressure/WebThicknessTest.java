package com.jsrm.motor.pressure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebThicknessTest {

    @Test
    void shouldComputeWebThickness() {
        // WHEN
        double webThickness = WebThickness.compute(20, 50);

        // THEN
        assertThat(webThickness).isEqualTo(15);
    }
}