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
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.*;
import static com.github.jbgust.jsrm.application.result.MotorClassification.*;
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
    void shouldComputeSRM_2014_motorWithAllSurfacesExposed() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withOuterSurface(EXPOSED)
                .withCoreSurface(EXPOSED)
                .withEndsSurface(EXPOSED)
                .withThroatDiameter(16.3577868)
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(default_SRM_2014_jsrmConfig);

        //THEN
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(19.82, offset(0.01));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(0.893, offset(0.001));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(6416, offset(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3820, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(138.5, offset(0.1));
        assertThat(result.getMotorClassification()).isEqualTo(L);
    }

    @Test
    void shouldComputeSRM_2014() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withNozzleExpansionRatio(8)
                .createJSRMConfig());

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(L);
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3602, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(130.6, offset(0.1));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(2060, offset(1.0));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(2.158, offset(0.001));
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(5.93, offset(0.01));
    }

    @Test
    void shouldComputeSRM_2014_customPressure() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(
                new JSRMConfigBuilder()
                        .withAmbiantPressureInMPa(0.9)
                        .withNozzleExpansionRatio(8)
                        .createJSRMConfig());

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(L);
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(2951, offset(2d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(107, offset(0.1));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(1648, offset(1.0));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(1.91, offset(0.01));
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(5.84, offset(0.01));
    }

    @Test
    void shouldComputeSRM_2014_nozzleEfficiency() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withNozzleEfficiency(1)
                .withNozzleExpansionRatio(8)
                .createJSRMConfig());

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(L);
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(4238, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(153.7, offset(0.1));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(2424, offset(1.0));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(2.158, offset(0.001));
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(5.93, offset(0.01));
    }

    @Test
    void shouldComputeSRM_2014_combustionEfficiency() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withCombustionEfficiencyRatio(1)
                .withNozzleExpansionRatio(8)
                .createJSRMConfig());

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(L);
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3716, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(134.8, offset(0.1));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(2112, offset(1.0));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(2.086, offset(0.001));
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(6.07, offset(0.01));
    }

    @Test
    void shouldComputeSRM_2014_nozzleErosion() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withNozzleErosionInMillimeter(2)
                .withNozzleExpansionRatio(8)
                .createJSRMConfig());

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(L);
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3493, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(126.6, offset(0.1));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(1658, offset(1.0));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(2.626, offset(0.001));
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(4.49, offset(0.01));
    }

    @Test
    void shouldComputeSRM_2014_erosiveBurningVelocityCoefficient() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withNozzleErosionInMillimeter(2)
                .withErosiveBurningVelocityCoefficient(0.5)
                .withErosiveBurningAreaRatioThreshold(0)
                .withNozzleExpansionRatio(8)
                .createJSRMConfig());

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(L);
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(3493, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(126.6, offset(0.1));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(1658, offset(1d));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(2.626, offset(0.001));
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(4.49, offset(0.01));
    }

    @Test
    void shouldComputeSRM_2014_erosiveBurningAreaRatioThreshold() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withNozzleErosionInMillimeter(2)
                .withErosiveBurningVelocityCoefficient(0.5)
                .withErosiveBurningAreaRatioThreshold(7)
                .withNozzleExpansionRatio(8)
                .createJSRMConfig());

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(L);
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(3563, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(129.2, offset(0.1));
        assertThat(result.getMaxThrustInNewton()).isCloseTo(4561, offset(1d));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(2.151, offset(0.001));
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(12.47, offset(0.01));
    }

    @Test
    void shouldComputeSRM_2014_density() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withDensityRatio(0.7)
                .withNozzleExpansionRatio(8)
                .createJSRMConfig());

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(K);
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(2461, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(121.1, offset(0.1));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(790, offset(1.0));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(3.343, offset(0.001));
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(2.48, offset(0.01));
    }

    /**
     * @see "SRM_2014 - EXPRAT_2.xls"
     */
    @Test
    void shouldComputeSRM_2014_nozzleExpansionRatio() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder().build();

        JSRMConfig config = new JSRMConfigBuilder()
                .withNozzleExpansionRatio(2)
                .createJSRMConfig();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(config);

        //THEN
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(5.93, offset(0.01));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(2.158, offset(0.001));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(1811, offset(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3209, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(116.3, offset(0.1));
        assertThat(result.getMotorClassification()).isEqualTo(L);
    }

    @Test
    void shouldComputeSRM_Herve() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withThroatDiameter(8.500639)
                .withOuterSurface(INHIBITED)
                .withCoreSurface(EXPOSED)
                .withEndsSurface(EXPOSED)
                .withChamberInnerDiameter(36)
                .withChamberLength(200)
                .withNumberOfSegment(3)
                .withGrainCoreDiameter(10)
                .withGrainOuterDiameter(34)
                .withGrainSegmentLength(58)
                .withPropellant(KNSU)
                .build();

        JSRMConfig jsrmConfig = new JSRMConfigBuilder()
                .withAmbiantPressureInMPa(0.101)
                .withDensityRatio(0.95)
                .withNozzleErosionInMillimeter(0)
                .withCombustionEfficiencyRatio(0.95)
                .withNozzleExpansionRatio(10)
                .withNozzleEfficiency(0.8)
                .withErosiveBurningAreaRatioThreshold(6)
                .withErosiveBurningVelocityCoefficient(0)
                .createJSRMConfig();
        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(jsrmConfig);

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(H);
        assertThat(result.getAverageThrustInNewton()).isEqualTo(337);

        assertThat(result.getThrustTimeInSecond()).isEqualTo(0.922, offset(0.001));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(379, offset(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(311, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(122.4, offset(0.1));
        // TODO investigate
        //assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(4.92, offset(0.01));
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
                .withOuterSurface(INHIBITED)
                .withEndsSurface(INHIBITED)
                .withCoreSurface(EXPOSED)
                .withThroatDiameter(17.6135459)
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(default_SRM_2014_jsrmConfig);

        //THEN
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(8.02, offset(0.01));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(2.605, offset(0.001));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(2918, offset(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3569, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(129.4, offset(0.1));
        assertThat(result.getMotorClassification()).isEqualTo(L);
    }

    /**
     * @see "SRM_2014 - OUTER_EXPOSED_ENDS INHIBITED.xls"
     */
    @Test
    void shouldComputeSRM_2014_motorWithInnerAndOuterExposedOnly() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withThroatDiameter(18.9616907)
                .withOuterSurface(EXPOSED)
                .withCoreSurface(EXPOSED)
                .withEndsSurface(INHIBITED)
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(default_SRM_2014_jsrmConfig);

        //THEN
        assertThat(result.getMaxChamberPressureInMPa()).isEqualTo(9.02, offset(0.01));
        assertThat(result.getThrustTimeInSecond()).isEqualTo(1.052, offset(0.001));
        assertThat(result.getMaxThrustInNewton()).isEqualTo(3825, offset(1.0));
        assertThat(result.getTotalImpulseInNewtonSecond()).isEqualTo(3750, offset(1d));
        assertThat(result.getSpecificImpulseInSecond()).isEqualTo(136.0, offset(0.1));
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

        assertThatThrownBy(simulation::run)
                .isInstanceOf(SimulationFailedException.class)
                .hasCauseExactlyInstanceOf(LineCalculatorException.class)
                .hasStackTraceContaining("Failed to compute PROPELLANT_BURN_RATE in line 3");
    }

    @Test
    void shouldUseLowAtmosphericPressure() {
        // GIVEN
        JSRMConfig jsrmConfig = new JSRMConfigBuilder().withAmbiantPressureInMPa(0.07).createJSRMConfig();

        // WHEN
        JSRMResult result = new JSRMSimulation(new SolidRocketMotorBuilder().build())
                .run(jsrmConfig);

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(L);
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(3602, Percentage.withPercentage(5));
    }

    @Test
    void shouldSpecifynumberOfCalculationLine() {
        // GIVEN
        JSRMConfig jsrmConfig = new JSRMConfigBuilder()
                .withNumberOfCalculationLine(400)
                .createJSRMConfig();

        // WHEN
        JSRMResult result = new JSRMSimulation(new SolidRocketMotorBuilder().build())
                .run(jsrmConfig);

        //THEN
        Percentage percentage = Percentage.withPercentage(2);

        assertThat(result.getMotorClassification()).isEqualTo(L);
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(3602, percentage);

        assertThat(result.getMaxChamberPressureInMPa()).isCloseTo(5.93, percentage);
        assertThat(result.getAverageChamberPressureInMPa()).isCloseTo(4.89, percentage);
        assertThat(result.getMaxThrustInNewton()).isCloseTo(2060, percentage);
        assertThat(result.getTotalImpulseInNewtonSecond()).isCloseTo(3602,  percentage);
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(130.6, percentage);
        assertThat(result.getSpecificImpulseInSecond()).isCloseTo(130.6, percentage);
        assertThat(result.getThrustTimeInSecond()).isCloseTo(2.1575, percentage);
        assertThat(result.getAverageThrustInNewton()).isCloseTo(1670, percentage);

        assertThat(result.getMotorParameters()).hasSize(400);
    }

}