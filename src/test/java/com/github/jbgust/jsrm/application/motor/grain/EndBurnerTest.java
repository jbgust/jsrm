package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.github.jbgust.jsrm.application.result.MotorClassification.G;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Percentage.withPercentage;

class EndBurnerTest extends MotorSimGrainTestConfiguration{

    @Test
    public void test() {
        EndBurner grain = new EndBurner(70d, 30d, 10d, 10d);
        double tweb = grain.webThickness();
        int totalGrainVolumeAtHalfBurn = 21363;

        assertThat(grain.getGrainVolume(.5))
                .as("GrainVolume")
                .isCloseTo(totalGrainVolumeAtHalfBurn, withPercentage(0.01));

        assertThat(grain.getXincp(834))
                .as("the famous xincp")
                .isCloseTo(tweb / 834d, withPercentage(0.01));

        assertThat(grain.getBurningArea(0.5))
                .as("Burning surfaces area")
                .isCloseTo(721, withPercentage(0.055));

        assertThat(grain.getGrainEndSurface(.5))
                .as("GrainEndSurface")
                .isCloseTo(grain.getBurningArea(0.5), withPercentage(0.01));
    }

    @Test
    public void runComputationWithEndBurnerGrain() {
        EndBurner grain = new EndBurner(70d, 30d, 10d, 10d);
        SolidRocketMotor motor = new SolidRocketMotor(
                new PropellantGrain(KNSU, grain),
                new CombustionChamber(40d, 75d), 6d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(motor);

        JSRMResult result = jsrmSimulation.run(motorSimConfig);

        assertThat(result.getNozzle().getNozzleExitDiameterInMillimeter()).isCloseTo(16.97056274847714, withPercentage(0.0001));
        assertThat(result.getMotorClassification()).isEqualTo(G);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(9, withPercentage(3.2));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(21, withPercentage(62));
        assertThat(result.getGrainMassInKg()).isCloseTo(0.0883, withPercentage(0.3));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(107, withPercentage(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(92.7, withPercentage(1.0));
    }

    @Test
    void shouldThrowExceptionIfOuterDiameterInvalid() {
        EndBurner grain = new EndBurner(70d, 0d, 10d, 10d);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Outer diameter should be > 0");
    }

    @Test
    void shouldThrowExceptionIfGrainLengthInvalid() {
        EndBurner grain = new EndBurner(0d, 30d, 10d, 10d);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Grain length should be > 0");
    }

    @Test
    void shouldThrowExceptionIfHoleDiameterGreaterThanOuterDiameter() {
        EndBurner grain = new EndBurner(70d, 30d, 31d, 10d);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Hole diameter should be < than outer diameter");
    }

    @Test
    void shouldThrowExceptionIfHoleLengthGreaterThanGrainLength() {
        EndBurner grain = new EndBurner(70d, 30d, 10d, 71d);
        SolidRocketMotor motor = new SolidRocketMotor(new PropellantGrain(KNSU, grain), new CombustionChamber(40d, 150d), 10d);

        assertThatThrownBy(() -> grain.checkConfiguration(motor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Hole length diameter should be < than grain length");
    }
}
