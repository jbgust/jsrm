package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.github.jbgust.jsrm.application.result.MotorClassification.H;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;

class CSlotGrainTest extends MotorSimGrainTestConfiguration {

    @Test
    public void test() {
        int numberOfSegment = 2;
        CSlotGrain grain = createCSlotTestGrain(numberOfSegment, EXPOSED);
        double tweb = grain.webThickness();
        int totalGrainVolumeAtHalfBurn = 25290;

        assertThat(grain.getGrainVolume(.5))
                .as("GrainVolume")
                .isCloseTo(totalGrainVolumeAtHalfBurn, withPercentage(0.12));

        double expectedEndGrainSurface = totalGrainVolumeAtHalfBurn / (numberOfSegment * grain.regressedLength(tweb / 2));
        assertThat(grain.getGrainEndSurface(.5))
                .as("GrainEndSurface")
                .isCloseTo(expectedEndGrainSurface, withPercentage(0.12));

        assertThat(grain.getXincp(834))
                .as("the famous xincp")
                .isCloseTo(tweb / 834d, withPercentage(0.01));

        assertThat(grain.getBurningArea(0.5))
                .as("Burning surfaces area")
                .isCloseTo(4747, withPercentage(0.033));
    }

    @Test
    public void runComputationWith2GrainExposed() {
        int numberOfSegment = 2;
        CSlotGrain grain = createCSlotTestGrain(numberOfSegment, EXPOSED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(KNSU, grain),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(73, withPercentage(1.4));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(181, withPercentage(0.5));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.149, withPercentage(0.3));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(114, withPercentage(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(166, withPercentage(1.0));
    }

    @Test
    public void runComputationWith2GrainInhibited() {
        int numberOfSegment = 2;
        CSlotGrain grain = createCSlotTestGrain(numberOfSegment, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(KNSU, grain),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, withPercentage(0.0001));
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(75, withPercentage(1.4));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(134, withPercentage(0.5));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.149, withPercentage(0.3));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(112, withPercentage(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(164, withPercentage(1.0));
    }

    @Test
    void shoulValidateGrainIfNoCore() {
        CSlotGrain grain = new CSlotGrain(30d, 0d, 5d, 15d, 7d, 2, 70d, INHIBITED);
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
        CSlotGrain grain = new CSlotGrain(30d, -1d, 5d, 15d, 7d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Core diameter should be positive");
    }

    @Test
    void shouldThrowExceptionIfInvalidOuterDiameter() {
        CSlotGrain grain = new CSlotGrain(0d, 10d, 5d, 15d, 7d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Outer diameter should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidLength() {
        CSlotGrain grain = new CSlotGrain(30d, 10d, 5d, 15d, 7d, 2, 0d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Grain length should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidCoreOuterDiameter() {
        CSlotGrain grain = new CSlotGrain(30d, 30d, 5d, 15d, 7d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Core diameter should be < than outer diameter");
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberLengthTooSmall() {
        CSlotGrain grain = new CSlotGrain(30d, 10d, 5d, 15d, 7d, 21, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combustion chamber length should be >= than Grain total length");
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberDiameterTooSmall() {
        CSlotGrain grain = new CSlotGrain(50d, 10d, 5d, 15d, 7d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combution chamber diameter should be >= than grain outer diameter");
    }

    @Test
    void shouldThrowExceptionIfInvalidNumberOfSegment() {
        CSlotGrain grain = new CSlotGrain(30d, 10d, 5d, 15d, 7d, 0, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Number of segment should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidSlotOffset() {
        CSlotGrain grain = new CSlotGrain(30d, 10d, 5d, 15d, -1d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Slot offset should be positive");
    }

    @Test
    void shouldThrowExceptionIfSlotOffsetOutsideGrain() {
        CSlotGrain grain = new CSlotGrain(30d, 10d, 5d, 15d, 15d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Slot offset should be < than grain radius");
    }

    @Test
    void shouldThrowExceptionIfInvalidSlotDepth() {
        CSlotGrain grain = new CSlotGrain(30d, 10d, 5d, 0d, 7d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Slot depth should be > 0");
    }

    @Test
    void shouldThrowExceptionIfSlotDepthOuterDiameterInvalid() {
        CSlotGrain grain = new CSlotGrain(30d, 10d, 5d, 30d, 7d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Slot depth should be <= than outer diameter");
    }

    @Test
    void shouldThrowExceptionIfInvalidSlotWidth() {
        CSlotGrain grain = new CSlotGrain(30d, 10d, 0d, 15d, 7d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Slot width should be > 0");
    }

    @Test
    void shouldThrowExceptionIfSlotWidthOuterDiameterInvalid() {
        CSlotGrain grain = new CSlotGrain(30d, 10d, 30d, 15d, 7d, 2, 70d, INHIBITED);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Slot width should be <= than outer diameter");
    }

    private CSlotGrain createCSlotTestGrain(int numberOfSegment, GrainSurface inhibited) {
        return new CSlotGrain(30d, 10d, 5d, 15d, 7d, numberOfSegment, 70d, inhibited);
    }
}
