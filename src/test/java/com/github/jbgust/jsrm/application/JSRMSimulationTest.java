package com.github.jbgust.jsrm.application;

import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.GrainSurface;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.exception.SimulationFailedException;
import com.github.jbgust.jsrm.calculation.exception.LineCalculatorException;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSB_FINE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JSRMSimulationTest {

    @Test
    void meteor(){
        SolidRocketMotor meteor = new SolidRocketMotor(
                new PropellantGrain(KNSB_FINE, 21.2, 8, 60, 1, GrainSurface.INHIBITED, GrainSurface.INHIBITED, GrainSurface.EXPOSED),
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