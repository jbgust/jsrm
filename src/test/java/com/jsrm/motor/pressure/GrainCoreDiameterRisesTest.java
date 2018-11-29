package com.jsrm.motor.pressure;

import com.jsrm.motor.PropellantGrain;
import com.jsrm.motor.utils.PropellantGrainBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.jsrm.motor.GrainSurface.EXPOSED;
import static com.jsrm.motor.GrainSurface.INHIBITED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class GrainCoreDiameterRisesTest {

    @ParameterizedTest
    @MethodSource("grainCoreDiameterWhenExposedByInterval")
    void shouldComputeGrainCoreDiameterWhenExposed(int interval, double expectedCoreDiameter) {
        // GIVEN
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withCoreSurface(EXPOSED)
                .build();

        WebRegression webRegression = new WebRegression(propellantGrain, 1000);
        GrainCoreDiameterRises grainCoreDiameter = new GrainCoreDiameterRises(webRegression);

        // THEN
        assertThat(grainCoreDiameter.compute(interval)).isEqualTo(expectedCoreDiameter);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void shouldComputeGrainCoreDiameterWhenInhibited(int interval) {
        // GIVEN
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withCoreSurface(INHIBITED)
                .build();

        WebRegression webRegression = new WebRegression(propellantGrain, 1000);
        GrainCoreDiameterRises grainCoreDiameter = new GrainCoreDiameterRises(webRegression);

        // THEN
        assertThat(grainCoreDiameter.compute(interval)).isEqualTo(propellantGrain.getCoreDiameter());
    }

    static Stream<Arguments> grainCoreDiameterWhenExposedByInterval() {
        return Stream.of(
                arguments(0, 10),
                arguments(1, 10 + 2 * 0.005),
                arguments(2, 10 + 4 * 0.005)
        );
    }
}