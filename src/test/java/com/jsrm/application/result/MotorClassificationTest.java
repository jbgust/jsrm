package com.jsrm.application.result;

import com.jsrm.application.exception.MotorClassificationOutOfBoundException;
import org.junit.jupiter.api.Test;

import static com.jsrm.application.result.MotorClassification.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MotorClassificationTest {

    @Test
    void shouldFindMotorClassification() {
        assertThat(getMotorClassification(20)).isEqualTo(D);
        assertThat(getMotorClassification(40)).isEqualTo(E);
        assertThat(getMotorClassification(40.00001)).isEqualTo(F);
    }

    @Test
    void shouldThrowErrorIfTotalImpulseIsOutOfBound() {
        double totalImpulseBelowAMotorClass = 1.25;
        double totalImpulseAboveVMotorClass = 5240001.0;

        assertThatThrownBy(() -> getMotorClassification(totalImpulseBelowAMotorClass))
                .isInstanceOf(MotorClassificationOutOfBoundException.class)
                .hasMessage("The total impulse of this motor is not in [A;V] classes");

        assertThatThrownBy(() -> getMotorClassification(totalImpulseAboveVMotorClass))
                .isInstanceOf(MotorClassificationOutOfBoundException.class)
                .hasMessage("The total impulse of this motor is not in [A;V] classes");
    }

}