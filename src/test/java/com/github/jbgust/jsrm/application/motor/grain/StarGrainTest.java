package com.github.jbgust.jsrm.application.motor.grain;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.github.jbgust.jsrm.application.result.MotorClassification.H;
import static com.github.jbgust.jsrm.application.result.MotorClassification.I;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;

import com.github.jbgust.jsrm.utils.KNSU_SRM_2014;
import org.junit.jupiter.api.Test;

import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.result.JSRMResult;

class StarGrainTest extends MotorSimGrainTestConfiguration{

    @Test
    public void test() {
        int numberOfSegment = 2;
        StarGrain grain = new StarGrain(30d, 5d, 15d, 5, numberOfSegment, 70d, EXPOSED);
        double tweb = grain.webThickness();
        int totalGrainVolumeAtHalfBurn = 36884;

        assertThat(grain.getGrainVolume(.5))
                .as("GrainVolume")
                .isCloseTo(totalGrainVolumeAtHalfBurn, withPercentage(0.01));

        double expectedEndGrainSurface = totalGrainVolumeAtHalfBurn / (numberOfSegment * grain.regressedLength(tweb / 2));
        assertThat(grain.getGrainEndSurface(.5))
                .as("GrainEndSurface")
                .isCloseTo(expectedEndGrainSurface, withPercentage(0.01));

        assertThat(grain.getXincp(834))
                .as("the famous xincp")
                .isCloseTo(tweb / 834d, withPercentage(0.01));

        assertThat(grain.getBurningArea(0.5))
                .as("Burning surfaces area")
                .isCloseTo(11029, withPercentage(0.015));
    }

    @Test
    public void shouldNotFailedWhenChamberPressureIsTooLow() {
        int numberOfSegment = 2;
        StarGrain grain = new StarGrain(30d, 5d, 15d, 5, numberOfSegment, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(KNSU, grain),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(
                new JSRMConfigBuilder()
                        .withCombustionEfficiencyRatio(0.97)
                        .withDensityRatio(.96)
                        .withNozzleExpansionRatio(8)
                        .withNumberOfCalculationLine(200)
                        .createJSRMConfig());

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(189, withPercentage(4.3));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(277, withPercentage(32.5));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.166, withPercentage(0.3));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(122, withPercentage(1.8));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(198, withPercentage(1.8));
    }

    @Test
    public void runComputationWith2StarGrain() {
        int numberOfSegment = 2;
        StarGrain grain = new StarGrain(30d, 5d, 15d, 5, numberOfSegment, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(new KNSU_SRM_2014(), grain),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(189, withPercentage(3.2));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(277, withPercentage(0.5));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.166, withPercentage(0.3));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(122, withPercentage(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(198, withPercentage(1.0));
    }

    @Test
    public void runComputationWith2StarGrainEndsInhibited() {
        int numberOfSegment = 2;
        StarGrain grain = new StarGrain(30d, 5d, 15d, 5, numberOfSegment, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(new KNSU_SRM_2014(), grain),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(192, withPercentage(3.2));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(365, withPercentage(0.5));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.166, withPercentage(0.3));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(123, withPercentage(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(199, withPercentage(1.0));
    }

    @Test
    public void runComputationWith4StarGrainEndsInhibited() {
        int numberOfSegment = 4;
        StarGrain grain = new StarGrain(30d, 5d, 15d, 5, numberOfSegment, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(new KNSU_SRM_2014(), grain),
                new CombustionChamber(40d, 300d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        assertThat(result.getMotorClassification()).isEqualTo(I);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(540, withPercentage(8.6));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(1067, withPercentage(1.4));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.331, withPercentage(0.3));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(135, withPercentage(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(438, withPercentage(1.0));
    }

    @Test
    void shouldThrowExceptionIfInvalidInnerDiameter() {
        StarGrain star = new StarGrain(30d, 0d, 15d, 5, 1, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Inner diameter should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInnerDiameterGreaterThanPointDiameter() {
        StarGrain star = new StarGrain(30d, 5d, 5d, 5, 1, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Inner diameter should be < than point diameter");
    }

    @Test
    void shouldThrowExceptionIfPointDiameterGreaterThanOuterDiameter() {
        StarGrain star = new StarGrain(30d, 5d, 30d, 5, 1, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Point diameter should be < than outer diameter");
    }

    @Test
    void shouldThrowExceptionIfInvalidPointCount() {
        StarGrain star = new StarGrain(30d, 5d, 15d, 0, 1, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Point count should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidOuterDiameter() {
        StarGrain star = new StarGrain(0d, 5d, 15d, 5, 1, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Outer diameter should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidLength() {
        StarGrain star = new StarGrain(30d, 5d, 15d, 5, 1, 0d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Grain length should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidInnerOuterDiameter() {
        StarGrain star = new StarGrain(30d, 30d, 15d, 5, 1, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Inner diameter should be < than outer diameter");
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberLengthTooSmall() {
        StarGrain star = new StarGrain(30d, 5d, 15d, 5, 3, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combustion chamber length should be >= than Grain total length");
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberDiameterTooSmall() {
        StarGrain star = new StarGrain(41d, 5d, 15d, 5, 1, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combution chamber diameter should be >= than grain outer diameter");
    }

    @Test
    void shouldThrowExceptionIfInvalidNumberOfSegment() {
        StarGrain star = new StarGrain(30d, 5d, 15d, 5, 0, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, star), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> star.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Number of segment should be > 0");
    }
}
