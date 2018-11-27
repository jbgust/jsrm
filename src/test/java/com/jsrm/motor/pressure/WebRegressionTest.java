package com.jsrm.motor.pressure;

import com.jsrm.motor.PropellantGrain;
import com.jsrm.motor.utils.PropellantGrainBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class WebRegressionTest {

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void shouldComputeWebRegressionForAnInterval(int interval) {
        // GIVEN
        double webRegressionStep = 0.005;
        PropellantGrain propellantGrain = new PropellantGrainBuilder().build();
        WebRegression webRegression = new WebRegression(propellantGrain, 1000);

        // THEN
        assertThat(webRegression.compute(interval)).isEqualTo(webRegressionStep*interval);
    }
}