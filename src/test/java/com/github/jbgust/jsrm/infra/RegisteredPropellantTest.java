package com.github.jbgust.jsrm.infra;

import com.github.jbgust.jsrm.application.RegisteredPropellant;
import com.github.jbgust.jsrm.application.exception.ChamberPressureOutOfBoundException;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.application.exception.UnregisteredPropellantException;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisteredPropellantTest {

    @ParameterizedTest
    @EnumSource(PropellantType.class)
    void nativePropellantShouldBeRegistered(PropellantType propellant) throws UnregisteredPropellantException {
        Assertions.assertThat(RegisteredPropellant.getSolidPropellant(propellant.getId())).isEqualTo(propellant);
    }

    @Test
    void shouldRegisterPropellant() {
        //GIVEN
        TestSolidPropellant solidPropellant = new TestSolidPropellant();

        //WHEN
        Integer propellantId = RegisteredPropellant.registerPropellant(solidPropellant);

        //THEN
        Assertions.assertThat(RegisteredPropellant.getSolidPropellant(propellantId)).isEqualTo(solidPropellant);
    }

    @Test
    void shouldNotRegisterPropellantThatHasBeenAlreadyRegistered() {
        //WHEN
        Integer propellantId = RegisteredPropellant.registerPropellant(KNDX);

        //THEN
        assertThat(propellantId).isEqualTo(KNDX.getId());
    }

    @Test
    void shouldThrowExceptionIfPropellantIdIsNotValid() {
        //GIVEN
        assertThatThrownBy(() -> RegisteredPropellant.getSolidPropellant(-1))
                .isInstanceOf(UnregisteredPropellantException.class)
                .hasMessage("The propellant with id (-1) is not registered.\n " +
                        "Use native propellant cf. PropellantType.class or register your propellant " +
                        "with RegisteredPropellant.registerPropellant(solidPropellant)");
    }

    private class TestSolidPropellant implements SolidPropellant {
        @Override
        public String getDescription() {
            return "Test propellant";
        }

        @Override
        public double getIdealMassDensity() {
            return 2;
        }

        @Override
        public double getK2Ph() {
            return 3;
        }

        @Override
        public double getK() {
            return 4;
        }

        @Override
        public double getEffectiveMolecularWeight() {
            return 5;
        }

        @Override
        public double getChamberTemperature() {
            return 6;
        }

        @Override
        public double getBurnRateCoefficient(double chamberPressure) throws ChamberPressureOutOfBoundException {
            return 7;
        }

        @Override
        public double getPressureExponent(double chamberPressure) throws ChamberPressureOutOfBoundException {
            return 8;
        }
    }
}