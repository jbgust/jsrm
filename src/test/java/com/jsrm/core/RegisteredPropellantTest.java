package com.jsrm.core;

import com.jsrm.motor.propellant.ChamberPressureOutOfBoundException;
import com.jsrm.motor.propellant.PropellantType;
import com.jsrm.motor.propellant.SolidPropellant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.jsrm.core.RegisteredPropellant.getSolidPropellant;
import static com.jsrm.core.RegisteredPropellant.registerPropellant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisteredPropellantTest {

    @ParameterizedTest
    @EnumSource(PropellantType.class)
    void nativePropellantShouldBeRegistered(PropellantType propellant) throws UnregisteredPropellantException{
        assertThat(getSolidPropellant(propellant.getId())).isEqualTo(propellant);
    }

    @Test
    void shouldRegisterPropellant() throws UnregisteredPropellantException {
        //GIVEN
        TestSolidPropellant solidPropellant = new TestSolidPropellant();

        //WHEN
        Integer propellantId = registerPropellant(solidPropellant);

        //THEN
        assertThat(getSolidPropellant(propellantId)).isEqualTo(solidPropellant);
    }

    @Test
    void shouldThrowExceptionIfPropellantIdIsNotValid() {
        //GIVEN
        assertThatThrownBy(() -> getSolidPropellant(-1))
                .isInstanceOf(UnregisteredPropellantException.class)
                .hasMessage("The propellant with id (-1) is not registered.\n " +
                        "Use native propellant cf. PropellantType.class or register your propellant " +
                        "with com.jsrm.core.RegisteredPropellant.registerPropellant(solidPropellant)");
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