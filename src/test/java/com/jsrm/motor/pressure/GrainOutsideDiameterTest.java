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

class GrainOutsideDiameterTest {

    @ParameterizedTest
    @MethodSource("grainOutsideDiameterWhenExposedByInterval")
    void shouldComputeGrainOutsideDiameterWhenExposed(int interval, double expectedOuterDiameter) {
        // GIVEN
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withOuterSurface(EXPOSED)
                .build();
        WebRegression webRegression = new WebRegression(propellantGrain, 1000);

        // THEN
        assertThat(new GrainOutsideDiameterRegression(webRegression).compute(interval)).isEqualTo(expectedOuterDiameter);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2 })
    void shouldComputeGrainOutsideDiameterWhenInhibited(int interval) {
        // GIVEN
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withOuterSurface(INHIBITED)
                .build();

        WebRegression webRegression = new WebRegression(propellantGrain, 1000);
        GrainOutsideDiameterRegression grainCoreDiameter = new GrainOutsideDiameterRegression(webRegression);

        // THEN
        assertThat(grainCoreDiameter.compute(interval)).isEqualTo(propellantGrain.getOuterDiameter());
    }

    static Stream<Arguments> grainOutsideDiameterWhenExposedByInterval() {
        return Stream.of(
                arguments(0, 20),
                arguments(1, 20 - 2 * 0.005),
                arguments(2, 20 - 4 * 0.005)
        );
    }
}