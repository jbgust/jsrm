package com.jsrm.motor.propellant;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.EnumSource;

import static com.jsrm.motor.propellant.PropellantType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropellantTypeTest {

    private static final double DELTA_PRESSURE = 0.00001d;

    private static int KNDX_lineNumber;
    private static int KNSB_FINE_lineNumber;

    @BeforeAll
    static void setUp() {
        KNDX_lineNumber = 0;
        KNSB_FINE_lineNumber = 0;
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/propellant/propellant_data.csv", numLinesToSkip = 1, delimiter = '|')
    void checkPropellantData(String propellantName, double idealGrainMassDensity, double k2Ph, double k, double effectiveMolecularWeight, double chamberTemperature) {
        SolidPropellant propellant = PropellantType.valueOf(propellantName);

        assertThat(propellant.getIdealMassDensity()).isEqualTo(idealGrainMassDensity);
        assertThat(propellant.getK2Ph()).isEqualTo(k2Ph);
        assertThat(propellant.getK()).isEqualTo(k);
        assertThat(propellant.getEffectiveMolecularWeight()).isEqualTo(effectiveMolecularWeight);
        assertThat(propellant.getChamberTemperature()).isEqualTo(chamberTemperature);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/propellant/KNDX_burnrate.csv", numLinesToSkip = 1, delimiter = '|')
    void checkKNDXBurnRateData(double minPressureInChamber, double maxPressureInChamber, double burnRateCoefficient, double pressureExponent) {
        assertPropellantBurnRateCoefficient(minPressureInChamber, maxPressureInChamber, burnRateCoefficient, KNDX, KNDX_lineNumber, 5);
        assertPropellantPressureExponent(minPressureInChamber, maxPressureInChamber, pressureExponent, KNDX, KNDX_lineNumber++, 5);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/propellant/KNSB_FINE_burnrate.csv", numLinesToSkip = 1, delimiter = '|')
    void checkKNSB_FINEBurnRateData(double minPressureInChamber, double maxPressureInChamber, double burnRateCoefficient, double pressureExponent) {
        assertPropellantBurnRateCoefficient(minPressureInChamber, maxPressureInChamber, burnRateCoefficient, KNSB_FINE, KNSB_FINE_lineNumber, 5);
        assertPropellantPressureExponent(minPressureInChamber, maxPressureInChamber, pressureExponent, KNSB_FINE, KNSB_FINE_lineNumber++, 5);
    }

    @Test
    void checkKNSB_COARSEBurnRateData() {
        double minPressureInChamber = 0.101;
        double maxPressureInChamber = 10.3;
        assertPropellantBurnRateCoefficient(minPressureInChamber, maxPressureInChamber, 5.13, KNSB_COARSE);
        assertPropellantPressureExponent(minPressureInChamber, maxPressureInChamber, 0.22, KNSB_COARSE);
    }

    @Test
    void checkKNMN_COARSEBurnRateData() {
        double minPressureInChamber = 0.101;
        double maxPressureInChamber = 10.3;
        assertPropellantBurnRateCoefficient(minPressureInChamber, maxPressureInChamber, 5.13, KNMN_COARSE);
        assertPropellantPressureExponent(minPressureInChamber, maxPressureInChamber, 0.22, KNMN_COARSE);
    }

    @Test
    void checkKNSUBurnRateData() {
        double minPressureInChamber = 0.101;
        double maxPressureInChamber = 10.3;
        assertPropellantBurnRateCoefficient(minPressureInChamber, maxPressureInChamber, 8.26, KNSU);
        assertPropellantPressureExponent(minPressureInChamber, maxPressureInChamber, 0.319, KNSU);
    }

    @Test
    void checkKNER_COARSEBurnRateData() {
        double minPressureInChamber = 0.101;
        double maxPressureInChamber = 10.3;
        assertPropellantBurnRateCoefficient(minPressureInChamber, maxPressureInChamber, 2.9, KNER_COARSE);
        assertPropellantPressureExponent(minPressureInChamber, maxPressureInChamber, 0.4, KNER_COARSE);
    }

    @ParameterizedTest
    @EnumSource(PropellantType.class)
    void shouldThrowExceptionIfKNDXBurnRateCoefficientIsUsedWithOutOfBoundChamberPressure(PropellantType propellantType) {
        assertThatThrownBy(() -> propellantType.getBurnRateCoefficient(0.09))
                .isInstanceOf(ChamberPressureOutOfBoundException.class)
                .hasMessageContaining(propellantType + " has no burn rate coefficient for this pressure (0.09) should be in range");
    }

    @ParameterizedTest
    @EnumSource(PropellantType.class)
    void shouldThrowExceptionIfKNDXPressureExponentIsUsedWithOutOfBoundChamberPressure(PropellantType propellantType) {
        assertThatThrownBy(() -> propellantType.getPressureExponent(0.09))
                .isInstanceOf(ChamberPressureOutOfBoundException.class)
                .hasMessageContaining(propellantType + " has no pressure exponent for this pressure (0.09) should be in range");
    }


    private void assertPropellantBurnRateCoefficient(double minPressureInChamber, double maxPressureInChamber, double burnRateCoefficient, PropellantType propellantType) {
        assertPropellantBurnRateCoefficient(minPressureInChamber, maxPressureInChamber, burnRateCoefficient, propellantType, 0, 1);
    }

    private void assertPropellantBurnRateCoefficient(double minPressureInChamber, double maxPressureInChamber, double burnRateCoefficient, PropellantType propellantType, int lineNumber, int numberOfLine) {
        assertThat(propellantType.getBurnRateCoefficient(minPressureInChamber)).isEqualTo(burnRateCoefficient);
        assertThat(propellantType.getBurnRateCoefficient(maxPressureInChamber - DELTA_PRESSURE)).isEqualTo(burnRateCoefficient);

        if (lineNumber == 0 && numberOfLine > 1) {
            assertThat(propellantType.getBurnRateCoefficient(maxPressureInChamber)).isNotEqualTo(burnRateCoefficient);
        } else if (numberOfLine - 1 == lineNumber) {
            if (numberOfLine > 1) {
                assertThat(propellantType.getBurnRateCoefficient(minPressureInChamber - DELTA_PRESSURE)).isNotEqualTo(burnRateCoefficient);
            }
            assertThat(propellantType.getBurnRateCoefficient(maxPressureInChamber)).isEqualTo(burnRateCoefficient);
        } else {
            assertThat(propellantType.getBurnRateCoefficient(minPressureInChamber - DELTA_PRESSURE)).isNotEqualTo(burnRateCoefficient);
            assertThat(propellantType.getBurnRateCoefficient(maxPressureInChamber)).isNotEqualTo(burnRateCoefficient);
        }

    }

    private void assertPropellantPressureExponent(double minPressureInChamber, double maxPressureInChamber, double burnRateCoefficient, PropellantType propellantType) {
        assertPropellantPressureExponent(minPressureInChamber, maxPressureInChamber, burnRateCoefficient, propellantType, 0, 1);
    }

    private void assertPropellantPressureExponent(double minPressureInChamber, double maxPressureInChamber, double pressureExponent, PropellantType propellantType, int lineNumber, int numberOfLine) {
        assertThat(propellantType.getPressureExponent(minPressureInChamber)).isEqualTo(pressureExponent);
        assertThat(propellantType.getPressureExponent(maxPressureInChamber - DELTA_PRESSURE)).isEqualTo(pressureExponent);

        if (lineNumber == 0 && numberOfLine > 1) {
            assertThat(propellantType.getPressureExponent(maxPressureInChamber)).isNotEqualTo(pressureExponent);
        } else if (numberOfLine - 1 == lineNumber) {
            if (numberOfLine > 1) {
                assertThat(propellantType.getPressureExponent(minPressureInChamber - DELTA_PRESSURE)).isNotEqualTo(pressureExponent);
            }
            assertThat(propellantType.getPressureExponent(maxPressureInChamber)).isEqualTo(pressureExponent);
        } else {
            assertThat(propellantType.getPressureExponent(minPressureInChamber - DELTA_PRESSURE)).isNotEqualTo(pressureExponent);
            assertThat(propellantType.getPressureExponent(maxPressureInChamber)).isNotEqualTo(pressureExponent);
        }
    }
}