package com.github.jbgust.jsrm.application.motor.propellant;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static org.assertj.core.api.Assertions.assertThat;

class PropellantGrainTest {

    @Test
    void shouldBuildPropellantGrain() {
        // GIVEN
        double outerDiameter = 20;
        double coreDiameter = 6;
        double segmentLength = 60;
        double numberOfSegment = 2;

        // WHEN
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, outerDiameter, coreDiameter, segmentLength, numberOfSegment,
                INHIBITED, EXPOSED, INHIBITED);

        // THEN
        assertThat(propellantGrain.getPropellant()).isEqualTo(KNDX);

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
        double segmentLength = 60;
        double numberOfSegment = 2;
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, outerDiameter, coreDiameter, segmentLength, numberOfSegment,
                INHIBITED, EXPOSED, INHIBITED);

        // WHEN
        double initialWebThickness = propellantGrain.getInitialWebThickness();

        // THEN
        assertThat(initialWebThickness).isEqualTo((outerDiameter-coreDiameter)/2);
    }
}