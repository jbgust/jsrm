package com.jsrm.application;

import static com.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.jsrm.application.result.MotorClassification.L;
import static com.jsrm.infra.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import org.junit.jupiter.api.Test;

import com.jsrm.application.motor.MotorChamber;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.application.motor.propellant.PropellantGrain;
import com.jsrm.application.result.JSRMResult;

class JSRMSimulationIT {

    @Test
    void shouldRunJSRMSimulation() {
        // GIVEN
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, 69d, 20d,
                115d, 4d,
                INHIBITED, EXPOSED, EXPOSED);
        MotorChamber motorChamber = new MotorChamber(75d, 470d);

        double throatDiameter = 17.3985248919802;

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, motorChamber,
                6d, throatDiameter, 0d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(solidRocketMotor);

        // WHEN
        JSRMResult jsrmResult = jsrmSimulation.run();

        // THEN
        assertThat(jsrmResult.getMotorClassification())
                .describedAs("Motor classification")
                .isEqualTo(L);

        assertThat(jsrmResult.getMaxChamberPressureInMPa())
                .describedAs("Max chamber pressure")
                .isEqualTo(5.93, offset(0.01d));

        assertThat(jsrmResult.getMaxThrustInNewton())
                .describedAs("Max thrust")
                .isEqualTo(2060, offset(1d));

        assertThat(jsrmResult.getTotalImpulseInNewtonSecond())
                .describedAs("Total impluse")
                .isEqualTo(3602,  offset(1d));

        assertThat(jsrmResult.getSpecificImpulseInSecond())
                .describedAs("Specific impulse")
                .isEqualTo(130.6, offset(0.1d));

        // TODO: Assert Nozzle,  avg Thrust, thrust time
        assertThat(jsrmResult.getNozzle().getOptimalNozzleExpansionRatio())
                .describedAs("Optimal nozzle expansion ratio")
                .isEqualTo(9.633, offset(0.001d));

    }
}