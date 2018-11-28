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

class GrainLengthRegressionTest {

    @ParameterizedTest
    @MethodSource("grainLengthWhenExposedByInterval")
    void shouldComputeGrainLengthWhenExposed(int interval, double expectedLength) {
        // GIVEN
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withEndsSurface(EXPOSED)
                .withNumberOfSegments(2)
                .build();
        WebRegression webRegression = new WebRegression(propellantGrain, 1000);

        // THEN
        assertThat(new GrainLengthRegression(webRegression).compute(interval)).isEqualTo(expectedLength);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void shouldComputeGrainLengthWhenInhibited(int interval) {
        // GIVEN
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withEndsSurface(INHIBITED)
                .withNumberOfSegments(2)
                .build();

        WebRegression webRegression = new WebRegression(propellantGrain, 1000);
        GrainLengthRegression grainLengthRegression = new GrainLengthRegression(webRegression);

        // THEN
        assertThat(grainLengthRegression.compute(interval)).isEqualTo(propellantGrain.getGrainLength());
    }

    static Stream<Arguments> grainLengthWhenExposedByInterval() {
        int numberOfSegment = 2;
        return Stream.of(
                arguments(0, 100),
                arguments(1, 100 - numberOfSegment * 2 * 0.005),
                arguments(2, 100 - numberOfSegment * 4 * 0.005)
        );
    }
}