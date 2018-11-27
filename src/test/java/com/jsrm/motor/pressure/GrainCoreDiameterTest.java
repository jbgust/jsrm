package com.jsrm.motor.pressure;

import com.jsrm.motor.PropellantGrain;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.jsrm.motor.GrainSurface.EXPOSED;
import static com.jsrm.motor.GrainSurface.INHIBITED;
import static com.jsrm.motor.propellant.Propellant.KNSB_FINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class GrainCoreDiameterTest {

    @ParameterizedTest
    @MethodSource("grainCoreDiameterByInterval")
    void shouldComputeGrainCoreDiameter(int interval, double expectedCoreDiameter) {
        // GIVEN
        WebRegression webRegression = new WebRegression(new PropellantGrain(KNSB_FINE, 20,10,50,1, INHIBITED, EXPOSED, EXPOSED), 1000);
        GrainCoreDiameter grainCoreDiameter = new GrainCoreDiameter(webRegression);

        // THEN
        assertThat(grainCoreDiameter.compute(interval)).isEqualTo(expectedCoreDiameter);

    }

    static Stream<Arguments> grainCoreDiameterByInterval() {
        return Stream.of(
                arguments(0, 10),
                arguments(1, 10 + 2 * 0.005),
                arguments(2, 10 + 4 * 0.005)
        );
    }
}