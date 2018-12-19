package com.jsrm.motor;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static com.jsrm.motor.GrainSurface.EXPOSED;
import static com.jsrm.motor.GrainSurface.INHIBITED;
import static com.jsrm.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;

class PropellantTypeGrainTest {

    @Test
    void shouldBuildPropellantGrain() {
        // GIVEN
        double outerDiameter = 20;
        double coreDiameter = 6;
        double segmentLenght = 60;
        double numberOfSegment = 2;

        // WHEN
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, outerDiameter, coreDiameter, segmentLenght, numberOfSegment,
                INHIBITED, EXPOSED, INHIBITED);

        // THEN
        assertThat(propellantGrain.getPropellantType()).isEqualTo(KNDX);

        assertThat(propellantGrain.getOuterSurface()).isEqualTo(INHIBITED);
        assertThat(propellantGrain.getEndsSurface()).isEqualTo(EXPOSED);
        assertThat(propellantGrain.getCoreSurface()).isEqualTo(INHIBITED);

        assertThat(propellantGrain.getGrainVolume()).isEqualTo(34306.19177, Offset.offset(0.00001));
    }

    @Test
    void shouldComputeInitialWebThickness() {
        // GIVEN
        double outerDiameter = 20;
        double coreDiameter = 6;
        double segmentLenght = 60;
        double numberOfSegment = 2;
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, outerDiameter, coreDiameter, segmentLenght, numberOfSegment,
                INHIBITED, EXPOSED, INHIBITED);

        // WHEN
        double initialWebThickness = propellantGrain.getInitialWebThickness();

        // THEN
        assertThat(initialWebThickness).isEqualTo((outerDiameter-coreDiameter)/2);
    }
}