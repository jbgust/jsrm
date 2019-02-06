package com.github.jbgust.jsrm.application;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.exception.SimulationFailedException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.GrainSurface;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.calculation.exception.LineCalculatorException;
import com.github.jbgust.jsrm.utils.PropellantGrainBuilder;
import com.github.jbgust.jsrm.utils.SolidRocketMotorBuilder;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSB_FINE;
import static com.github.jbgust.jsrm.application.result.MotorClassification.L;
import static org.assertj.core.api.Assertions.*;

class JSRMSimulationTest {

    //see SRM_2014.xls
    private final JSRMConfig default_SRM_2014_jsrmConfig = new JSRMConfigBuilder()
            .withNozzleExpansionRatio(8)
            .createJSRMConfig();

    /**
     * @see "SRM_2014 - OUTER_EXPOSED.xls"
     */
    @Test
    void shouldComputeSRM_2014_motorWithOuterExposed() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withThroatDiameter(26.338369575)
                .withOuterSurface(EXPOSED)
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(default_SRM_2014_jsrmConfig);

        //THEN
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(5.78, offset(0.01));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(1.370, offset(0.001));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(4592, offset(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3484, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(126.3, offset(0.1));
        assertThat(result.getMotorClassification()).isEqualTo(L);
    }

    @Test
    void shouldCheckSolidRocketMotor() {
        //GIVEN
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withNumberOfSegments(2)
                .withSegmentLength(45)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 89), 5d);


        assertThatThrownBy( () -> new JSRMSimulation(solidRocketMotor).run(default_SRM_2014_jsrmConfig))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combustion chamber length should be >= than Grain total length");
    }

    /**
     * @see "SRM_2014 - ONLY_INNER_EXPOSED.xls"
     */
    @Test
    void shouldComputeSRM_2014_motorWithInnerExposedOnly() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withThroatDiameter(21.05621206)
                .withOuterSurface(INHIBITED)
                .withEndsSurface(INHIBITED)
                .withCoreSurface(EXPOSED)
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(default_SRM_2014_jsrmConfig);

        //THEN
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(5.69, offset(0.01));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(3.065, offset(0.001));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(2884, offset(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3382, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(122.6, offset(0.1));
        assertThat(result.getMotorClassification()).isEqualTo(L);
    }

    /**
     * @see "SRM_2014 - OUTER_EXPOSED_ENDS INHIBITED.xls"
     */
    @Test
    void shouldComputeSRM_2014_motorWithInnerAndOuterExposedOnly() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withThroatDiameter(23.91391624)
                .withOuterSurface(EXPOSED)
                .withCoreSurface(EXPOSED)
                .withEndsSurface(INHIBITED)
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(default_SRM_2014_jsrmConfig);

        //THEN
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(5.93, offset(0.01));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(0.983, offset(0.001));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(3892, offset(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3652, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(132.4, offset(0.1));
        assertThat(result.getMotorClassification()).isEqualTo(L);
    }

    @Test
    void shouldThrowException(){
        SolidRocketMotor meteor = new SolidRocketMotor(
                new PropellantGrain(KNSB_FINE, 21.2, 8, 60, 1, GrainSurface.INHIBITED, GrainSurface.INHIBITED, EXPOSED),
                new CombustionChamber(21.2, 60),
                6.0
        );
        JSRMSimulation simulation = new JSRMSimulation(meteor);

        assertThatThrownBy(()->simulation.run())
                .isInstanceOf(SimulationFailedException.class)
                .hasCauseExactlyInstanceOf(LineCalculatorException.class)
                .hasStackTraceContaining("Failed to compute PROPELLANT_BURN_RATE in line 3");
    }

}