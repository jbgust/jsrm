package com.github.jbgust.jsrm.infra;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.utils.PropellantGrainBuilder;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.infra.SolidRocketMotorChecker.check;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SolidRocketMotorCheckerTest {

    @Test
    void shouldCheckSolidRocketMotor(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 8d);

        assertDoesNotThrow(() -> check(solidRocketMotor));
    }

    @Test
    void shouldThrowExceptionIfCoreDiameterIsLessThanThroatDiameter(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withCoreDiameter(8)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 8.1d);

        assertThatThrownBy(() -> check(solidRocketMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Throat diameter should be >= than grain core diameter");
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberDiameterIsLessThanGrainOuterDiameter(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withOuterDiameter(20.1)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 5d);

        assertThatThrownBy(() -> check(solidRocketMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combution chamber diameter should be >= than grain outer diameter");
    }

    @Test
    void shouldThrowExceptionIfGrainOuterDiameterIsLessThanCoreDiameter(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withOuterDiameter(20)
                .withCoreDiameter(20)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 5d);

        assertThatThrownBy(() -> check(solidRocketMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Grain outer diameter should be > than grain core diameter");
    }

    @Test
    void shouldThrowExceptionIfGrainLengthIsGreaterThanCombustionChamberLength(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withNumberOfSegments(2)
                .withSegmentLength(45)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 89), 5d);

        assertThatThrownBy(() -> check(solidRocketMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combustion chamber length should be >= than Grain total length");
    }

}