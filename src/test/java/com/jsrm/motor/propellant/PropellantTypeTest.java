package com.jsrm.motor.propellant;

import org.junit.jupiter.api.Test;

import static com.jsrm.motor.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropellantTypeTest {

    @Test
    void shouldThrowExceptionIfKNDXBurnRateCoefficientIsUsedWithOutOfBoundChamberPressure() {
        assertThatThrownBy(() -> KNDX.getBurnRateCoefficient(0.09))
                .isInstanceOf(ChamberPressureOutOfBoundException.class)
                .hasMessage("KNDX has no burn rate coefficient for this pressure (0.09) should be in range [0.1..+∞)");
    }

    @Test
    void shouldThrowExceptionIfKNDXPressureExponentIsUsedWithOutOfBoundChamberPressure() {
        assertThatThrownBy(() -> KNDX.getPressureExponent(0.09))
                .isInstanceOf(ChamberPressureOutOfBoundException.class)
                .hasMessage("KNDX has no pressure exponent for this pressure (0.09) should be in range [0.1..+∞)");
    }

}