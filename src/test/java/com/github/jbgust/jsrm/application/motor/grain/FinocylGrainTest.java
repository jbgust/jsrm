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
import static com.github.jbgust.jsrm.application.result.MotorClassification.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;
import static org.assertj.core.data.Percentage.withPercentage;

public class FinocylGrainTest extends MotorSimGrainTestConfiguration{

    @Test
    public void test() {
        FinocylGrain finocyl = new FinocylGrain(30d, 10d, 2d, 20d, 5, 70d, 1, EXPOSED);
        double tweb = finocyl.webThickness();
        int totalGrainVolumeAtHalfBurn = 12813;

        assertThat(finocyl.getGrainVolume(.5))
                .as("GrainVolume")
                .isCloseTo(totalGrainVolumeAtHalfBurn, offset(1d));

        assertThat(finocyl.getGrainEndSurface(.5))
                .as("GrainEndSurface")
                .isCloseTo(totalGrainVolumeAtHalfBurn / finocyl.regressedLength(tweb / 2), offset(1d));

        assertThat(finocyl.getXincp(834))
                .as("the famous xincp")
                .isCloseTo(tweb / 834d, offset(0.0000001));

        assertThat(finocyl.getBurningArea(4.05 / tweb))
                .as("Burning surfaces area")
                .isCloseTo(6717, offset(1d));
    }

    @Test
    public void runComputationWithFinocyl() {
        FinocylGrain finocyl = new FinocylGrain(30d, 10d, 2d, 20d, 5, 70d, 1, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(
                new PropellantGrain(new KNSU_SRM_2014(), finocyl),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(finocylMotor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, offset(0.00001));
        assertThat(result.getMotorClassification()).isEqualTo(G);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(75, withPercentage(2.7));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.0734, offset(0.001d));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(115, offset(1d));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(82.5, offset(0.4));
    }

    @Test
    public void runComputationWith2FinocylGrain() {
        FinocylGrain finocyl = new FinocylGrain(30d, 10d, 2d, 20d, 5, 70d, 2, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(
                new PropellantGrain(new KNSU_SRM_2014(), finocyl),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(finocylMotor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, offset(0.00001));
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(209, withPercentage(2.7));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(392, offset(0.8));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.147, offset(0.001d));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(125, offset(1.004d));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(180, offset(1.4));
    }

    @Test
    public void runComputationWith2FinocylGrainEndsInhibited() {
        FinocylGrain finocyl = new FinocylGrain(30d, 10d, 2d, 20d, 5, 70d, 2, INHIBITED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(
                new PropellantGrain(new KNSU_SRM_2014(), finocyl),
                new CombustionChamber(40d, 150d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(finocylMotor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, offset(0.00001));
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(210, offset(8L));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(439, offset(0.3d));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.147, offset(0.001d));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(125, offset(1d));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(180, offset(1d));
    }

    @Test
    public void runComputationWith4FinocylGrainEndsInhibited() {
        FinocylGrain finocyl = new FinocylGrain(30d, 0.01d, 3d, 20d, 3, 70d, 4, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(
                new PropellantGrain(new KNSU_SRM_2014(), finocyl),
                new CombustionChamber(40d, 300d), 10d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(finocylMotor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(28.284271247461902, offset(0.00001));
        assertThat(result.getMotorClassification()).isEqualTo(I);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(378, withPercentage(6));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(956, offset(7d));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.315, offset(0.001d));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(133, offset(0.4d));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(412, offset(0.3d));
    }

    @Test
    void shouldThrowExceptionIfInvalidInnerDiameter() {
        FinocylGrain finocyl = new FinocylGrain(30d, 0d, 2d, 20d, 5, 70d, 2, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, finocyl), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> finocyl.checkConfiguration(finocylMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Inner diameter should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidOuterDiameter() {
        FinocylGrain finocyl = new FinocylGrain(0d, 10d, 2d, 20d, 5, 70d, 2, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, finocyl), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> finocyl.checkConfiguration(finocylMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Outer diameter should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidLength() {
        FinocylGrain finocyl = new FinocylGrain(30d, 10d, 2d, 20d, 5, 0d, 2, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, finocyl), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> finocyl.checkConfiguration(finocylMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Grain length should be > 0");
    }

    @Test
    void shouldThrowExceptionIfInvalidInnerOuterDiameter() {
        FinocylGrain finocyl = new FinocylGrain(30d, 40d, 2d, 20d, 5, 70d, 2, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, finocyl), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> finocyl.checkConfiguration(finocylMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Inner diameter should be < than outer diameter");
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberLengthTooSmall() {
        FinocylGrain finocyl = new FinocylGrain(30d, 10d, 2d, 20d, 5, 70d, 20, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, finocyl), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> finocyl.checkConfiguration(finocylMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combustion chamber length should be >= than Grain total length");
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberDiameterTooSmall() {
        FinocylGrain finocyl = new FinocylGrain(41d, 10d, 2d, 20d, 5, 70d, 2, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, finocyl), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> finocyl.checkConfiguration(finocylMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combution chamber diameter should be >= than grain outer diameter");
    }

    @Test
    void shouldThrowExceptionIfInvalidNumberOfSegment() {
        FinocylGrain finocyl = new FinocylGrain(30d, 10d, 2d, 20d, 5, 70d, 0, EXPOSED);
        SolidRocketMotor finocylMotor = new SolidRocketMotor(new PropellantGrain(PropellantType.KNSU, finocyl), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> finocyl.checkConfiguration(finocylMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Number of segment should be > 0");
    }
}
