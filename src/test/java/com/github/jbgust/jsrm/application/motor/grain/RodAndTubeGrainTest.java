package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.infra.function.HollowCircleAreaFunction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.github.jbgust.jsrm.application.result.MotorClassification.G;
import static java.lang.Math.PI;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

class RodAndTubeGrainTest extends MotorSimGrainTestConfiguration {

    @Test
    public void shouldUseRodThicknessToComputeXINCP() {
        RodAndTubeGrain grain = new RodAndTubeGrain(15d, 30d, 20d, 2, 70d, EXPOSED);

        assertThat(grain.getXincp(453)).isEqualTo(15d / 2d / 453d);
    }

    @Test
    public void shouldUseTubeThicknessToComputeXINCP() {
        RodAndTubeGrain grain = new RodAndTubeGrain(1d, 30d, 20d, 2, 70d, EXPOSED);

        assertThat(grain.getXincp(453)).isEqualTo((30d - 20d) / 2d / 453d);
    }

    @Test
    public void shouldComputeVolumeAtTheEnd() {
        int numberOfSegment = 2;
        RodAndTubeGrain grain = new RodAndTubeGrain(15d, 30d, 20d, numberOfSegment, 70d, EXPOSED);

        assertThat(grain.getGrainVolume(1)).isEqualTo(0);
    }

    @Test
    public void shouldComputeRodVolumeExposed() {
        int numberOfSegment = 2;
        RodAndTubeGrain grain = new RodAndTubeGrain(15d, 30d, 20d, numberOfSegment, 70d, EXPOSED);

        double regression = 5.75;
        double rodArea = Math.pow(15d / 2 - regression, 2) * PI;
        double segmentLength = 58.5;

        assertThat(grain.getGrainVolume(regression / grain.webThickness())).isEqualTo(rodArea * segmentLength * numberOfSegment);
    }

    @Test
    public void shouldComputeRodVolumeInhibited() {
        int numberOfSegment = 2;
        double segmentLength = 70d;
        RodAndTubeGrain grain = new RodAndTubeGrain(15d, 30d, 20d, numberOfSegment, segmentLength, INHIBITED);

        double regression = 5.75;
        double rodArea = Math.pow(15d / 2 - regression, 2) * PI;

        assertThat(grain.getGrainVolume(regression / grain.webThickness())).isEqualTo(rodArea * segmentLength * numberOfSegment);
    }

    @Test
    public void shouldComputeTubeVolumeExposed() {
        int numberOfSegment = 2;
        RodAndTubeGrain grain = new RodAndTubeGrain(4d, 30d, 20d, numberOfSegment, 70d, EXPOSED);

        double regression = 2.5;
        double segmentLength = 70 - 2 * 2.5;
        double tubeArea = new HollowCircleAreaFunction().runFunction(30d, 20d + 5d);
        assertThat(grain.getGrainVolume(regression / grain.webThickness())).isEqualTo(tubeArea * segmentLength * numberOfSegment);
    }

    @Test
    public void shouldComputeTubeVolumeInhibited() {
        int numberOfSegment = 2;
        double segmentLength = 70d;
        RodAndTubeGrain grain = new RodAndTubeGrain(4d, 30d, 20d, numberOfSegment, segmentLength, INHIBITED);

        double regression = 2.5;
        double tubeArea = new HollowCircleAreaFunction().runFunction(30d, 20d + 5d);
        assertThat(grain.getGrainVolume(regression / grain.webThickness())).isEqualTo(tubeArea * segmentLength * numberOfSegment);
    }

    @Test
    public void shouldComputeRodAndTubeVolume() {
        int numberOfSegment = 2;
        double segmentLength = 70d;
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 30d, 20d, numberOfSegment, segmentLength, INHIBITED);

        double regression = 2.5;
        double tubeArea = new HollowCircleAreaFunction().runFunction(30d, 20d + 5d);
        double rodArea = Math.pow(10d / 2 - regression, 2) * PI;

        assertThat(grain.getGrainVolume(regression / grain.webThickness()))
                .isCloseTo((tubeArea + rodArea) * segmentLength * numberOfSegment, withPercentage(0.00001));

        Assertions.assertThat(grain.getGrainOuterDiameter(0.7)).isEqualTo(30d);
    }

    @Test
    public void shouldComputeRodAndTubeEndSurface() {
        int numberOfSegment = 2;
        double segmentLength = 70d;
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 30d, 20d, numberOfSegment, segmentLength, INHIBITED);

        double regression = 2.5;
        double tubeArea = new HollowCircleAreaFunction().runFunction(30d, 20d + 5d);
        double rodArea = Math.pow(10d / 2 - regression, 2) * PI;

        assertThat(grain.getGrainEndSurface(regression / grain.webThickness()))
                .isCloseTo(tubeArea + rodArea, withPercentage(0.00001));
    }

    @Test
    public void shouldComputeRodAndTubeBurningAreaInhibited() {
        int numberOfSegment = 2;
        double segmentLength = 70d;
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 30d, 20d, numberOfSegment, segmentLength, INHIBITED);

        double regression = 2.5;

        assertThat(grain.getBurningArea(regression / grain.webThickness()))
                .isCloseTo(((20d + 5d) + (10d - 5d)) * PI * segmentLength * numberOfSegment, withPercentage(0.00001));
    }

    @Test
    public void shouldComputeRodAndTubeBurningAreaExposed() {
        int numberOfSegment = 2;
        double segmentLength = 70d;
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 30d, 20d, numberOfSegment, segmentLength, EXPOSED);

        double regression = 2.5;
        double tubeArea = new HollowCircleAreaFunction().runFunction(30d, 20d + 5d);
        double rodArea = Math.pow(10d / 2 - regression, 2) * PI;

        double endSurfaces = (tubeArea + rodArea) * 2 * numberOfSegment;
        assertThat(grain.getBurningArea(regression / grain.webThickness()))
                .isCloseTo(((20d + 5d) + (10d - 5d)) * PI * (segmentLength - 2.5 * 2) * numberOfSegment + endSurfaces, withPercentage(0.00001));
    }


    @Test
    public void runComputationWith2GrainExposed() {
        int numberOfSegment = 2;
        RodAndTubeGrain grain = createRodTubeTestGrain(numberOfSegment, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(KNSU, grain),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        Assertions.assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        Assertions.assertThat(result.getMotorClassification()).isEqualTo(G);
        Assertions.assertThat(result.getAverageThrustInNewton()).isCloseTo(319, withPercentage(10));
        Assertions.assertThat(result.getMaxThrustInNewton()).isCloseTo(438, withPercentage(0.5));
        Assertions.assertThat(result.getGrainMassInKg()).isCloseTo(0.1196, withPercentage(0.04));
        Assertions.assertThat(result.getSpecificImpulseInSecond()).isCloseTo(128, withPercentage(1.0));
        Assertions.assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(151, withPercentage(1.1));
    }

    @Test
    public void runComputationWith2GrainInhibited() {
        int numberOfSegment = 2;
        RodAndTubeGrain grain = createRodTubeTestGrain(numberOfSegment, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(KNSU, grain),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        Assertions.assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        Assertions.assertThat(result.getMotorClassification()).isEqualTo(G);
        Assertions.assertThat(result.getAverageThrustInNewton()).isCloseTo(318, withPercentage(9.5));
        Assertions.assertThat(result.getMaxThrustInNewton()).isCloseTo(367, withPercentage(0.14));
        Assertions.assertThat(result.getGrainMassInKg()).isCloseTo(0.1196, withPercentage(0.04));
        Assertions.assertThat(result.getSpecificImpulseInSecond()).isCloseTo(128, withPercentage(1.0));
        Assertions.assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(151, withPercentage(1.1));
    }

    @Test
    void shouldThrowExceptionIfInvalidRodDiameter() {
        RodAndTubeGrain grain = new RodAndTubeGrain(0d, 30d, 20d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Rod diameter should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidRodDiameterTubeInnerDiameter() {
        RodAndTubeGrain grain = new RodAndTubeGrain(20d, 30d, 20d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Rod diameter should be < than tube inner diameter");
    }

    @Test
    void shouldThrowExceptionIfInvalidTubeOuterDiameter() {
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 0d, 20d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Tube outer diameter should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidTubeDiameters() {
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 30d,30d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Tube outer diameter should be > than tube inner diameter");
    }

    @Test
    void shouldThrowExceptionIfInvalidTubeOuterDiameterChamberDiameter() {
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 41d, 20d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combution chamber diameter should be >= than tube outer diameter");
    }

    @Test
    void shouldThrowExceptionIfInvalidNumberSegment() {
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 30d, 20d, 0, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Number of segment should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidGrainLength() {
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 30d, 20d, 2, 0d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Grain length should be > 5.0");
    }

    @Test
    void shouldThrowExceptionIfInvalidTotalGrainLength() {
        RodAndTubeGrain grain = new RodAndTubeGrain(10d, 30d, 20d, 4, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combustion chamber length should be >= than Grain total length");
    }

    private RodAndTubeGrain createRodTubeTestGrain(int numberOfSegment, GrainSurface grainSurface) {
        return new RodAndTubeGrain(10d, 30d, 20d, numberOfSegment, 70d, grainSurface);
    }

}
