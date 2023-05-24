package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.utils.KNSU_SRM_2014;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.github.jbgust.jsrm.application.result.MotorClassification.H;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;

class MoonBurnerGrainTest extends MotorSimGrainTestConfiguration{

    @Test
    public void test() {
        int numberOfSegment = 2;
        MoonBurnerGrain grain = createMoonBurnerTestGrain(numberOfSegment, EXPOSED);
        double tweb = grain.webThickness();
        int totalGrainVolumeAtHalfBurn = 28869;

        assertThat(grain.getGrainVolume(.5))
                .as("GrainVolume")
                .isCloseTo(totalGrainVolumeAtHalfBurn, withPercentage(0.16));

        double expectedEndGrainSurface = totalGrainVolumeAtHalfBurn / (numberOfSegment * grain.regressedLength(tweb / 2));
        assertThat(grain.getGrainEndSurface(.5))
                .as("GrainEndSurface")
                .isCloseTo(expectedEndGrainSurface, withPercentage(0.16));

        assertThat(grain.getXincp(834))
                .as("the famous xincp")
                .isCloseTo(tweb / 834d, withPercentage(0.01));

        assertThat(grain.getBurningArea(0.5))
                .as("Burning surfaces area")
                .isCloseTo(6351, withPercentage(0.07));
    }

    @Test
    public void runComputationWith2GrainExposed() {
        int numberOfSegment = 2;
        MoonBurnerGrain grain = createMoonBurnerTestGrain(numberOfSegment, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(new KNSU_SRM_2014(), grain),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(98, withPercentage(2.1));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(204, withPercentage(0.5));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.160, withPercentage(0.3));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(116, withPercentage(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(181, withPercentage(1.0));
    }

    @Test
    public void runComputationWith2GrainInhibited() {
        int numberOfSegment = 2;
        MoonBurnerGrain grain = createMoonBurnerTestGrain(numberOfSegment, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(new KNSU_SRM_2014(), grain),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(99, withPercentage(3.1));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(193, withPercentage(0.7));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.160, withPercentage(0.3));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(114, withPercentage(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(179, withPercentage(1.0));
    }

    @Test
    void shoulValidateGrainIfNoCoreOffset() {
        MoonBurnerGrain grain = new MoonBurnerGrain(30d, 10d, 0d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        boolean exceptionThrow = false;
        try {
            grain.checkConfiguration(motor);
        } catch (Exception e) {
            exceptionThrow = true;
        }

        assertThat(exceptionThrow).isFalse();
    }

    @Test
    void shouldThrowExceptionIfInvalidCoreDiameter() {
        MoonBurnerGrain grain = new MoonBurnerGrain(30d, 0d, 5d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Core diameter should be positive");
    }

    @Test
    void shouldThrowExceptionIfInvalidOuterDiameter() {
        MoonBurnerGrain grain = new MoonBurnerGrain(0d, 10d, 5d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Outer diameter should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidLength() {
        MoonBurnerGrain grain = new MoonBurnerGrain(30d, 10d, 5d, 2, 0d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Grain length should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidCoreOuterDiameter() {
        MoonBurnerGrain grain = new MoonBurnerGrain(30d, 30d, 5d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Core diameter should be < than outer diameter");
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberLengthTooSmall() {
        MoonBurnerGrain grain = new MoonBurnerGrain(30d, 10d, 5d, 5, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combustion chamber length should be >= than Grain total length");
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberDiameterTooSmall() {
        MoonBurnerGrain grain = new MoonBurnerGrain(50d, 10d, 5d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combution chamber diameter should be >= than grain outer diameter");
    }

    @Test
    void shouldThrowExceptionIfInvalidNumberOfSegment() {
        MoonBurnerGrain grain = new MoonBurnerGrain(30d, 10d, 5d, 0, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Number of segment should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidSlotOffset() {
        MoonBurnerGrain grain = new MoonBurnerGrain(30d, 10d, -1d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Core offset should be positive");
    }

    @Test
    void shouldThrowExceptionIfSlotOffsetOutsideGrain() {
        MoonBurnerGrain grain = new MoonBurnerGrain(30d, 10d, 20d, 2, 70d, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Core offset should be inside the grain");
    }


    private MoonBurnerGrain createMoonBurnerTestGrain(int numberOfSegment, GrainSurface inhibited) {
        return new MoonBurnerGrain(30d, 10d, 5d, numberOfSegment, 70d, inhibited);
    }
}
