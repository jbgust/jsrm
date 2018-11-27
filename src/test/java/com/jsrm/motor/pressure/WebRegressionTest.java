package com.jsrm.motor.pressure;

import com.jsrm.motor.PropellantGrain;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.jsrm.motor.GrainSurface.EXPOSED;
import static com.jsrm.motor.GrainSurface.INHIBITED;
import static com.jsrm.motor.propellant.Propellant.KNSB_FINE;
import static org.assertj.core.api.Assertions.assertThat;

class WebRegressionTest {

    @ParameterizedTest
    @ValueSource(ints = { 0, 1 })
    void shouldComputeWebRegressionForAnInterval(int interval) {
        // GIVEN
        double webRegressionStep = 0.005;
        PropellantGrain propellantGrain = new PropellantGrain(KNSB_FINE, 20,10,50,1, INHIBITED, EXPOSED, INHIBITED);
        WebRegression webRegression = new WebRegression(propellantGrain, 1000);

        // THEN
        assertThat(webRegression.compute(interval)).isEqualTo(webRegressionStep*interval);
    }
}