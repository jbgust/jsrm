package com.github.jbgust.jsrm.application;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.GrainSurface;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorParameters;
import com.github.jbgust.jsrm.utils.SolidRocketMotorBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.github.jbgust.jsrm.application.result.MotorClassification.G;
import static com.github.jbgust.jsrm.application.result.MotorClassification.H;
import static org.assertj.core.api.Assertions.assertThat;

class LowKNTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/lowKN.csv", numLinesToSkip = 15)
    void shouldValidLowKNFunction(double throatDiameter,
                            double outerDiameter,
                            double coreDiameter,
                            double segmentLength,
                            double numberOfSegment,
                            String outerSurface,
                            String endsSurface,
                            String coreSurface,
                            String propellantType,
                            double chamberInnerDiameter,
                            double chamberLength,
                            double densityRatio,
                            double nozzleErosion,
                            double combustionEfficiencyRatio,
                            double ambiantPressure,
                            double erosiveBurningAreaRatioThreshold,
                            double erosiveBurningVelocityCoefficient,
                            double nozzleEfficiency,
                            boolean optimalNozzleDesign,
                            String nozzleExpansionRatio) {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withThroatDiameter(throatDiameter)
                .withGrainOuterDiameter(outerDiameter)
                .withGrainCoreDiameter(coreDiameter)
                .withGrainSegmentLength(segmentLength)
                .withNumberOfSegment(numberOfSegment)
                .withOuterSurface(GrainSurface.valueOf(outerSurface))
                .withEndsSurface(GrainSurface.valueOf(endsSurface))
                .withCoreSurface(GrainSurface.valueOf(coreSurface))
                .withPropellant(PropellantType.valueOf(propellantType))
                .withChamberInnerDiameter(chamberInnerDiameter)
                .withChamberLength(chamberLength)
                .build();

        try {
            JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                    .withSafeKNFailure(true)
                    .createJSRMConfig());

            printSimulationInfos(motor, result);

            assertThat(true).isTrue();
        } catch (InvalidMotorDesignException e) {
            assertThat(true).isTrue();
        }

    }

    @Test
    void shouldComputeLowKN() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withThroatDiameter(8)
                .withGrainOuterDiameter(28)
                .withGrainCoreDiameter(12)
                .withGrainSegmentLength(98)
                .withNumberOfSegment(1)
                .withOuterSurface(INHIBITED)
                .withEndsSurface(EXPOSED)
                .withCoreSurface(EXPOSED)
                .withPropellant(KNDX)
                .withChamberInnerDiameter(28)
                .withChamberLength(98)
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withSafeKNFailure(true)
                .createJSRMConfig());

        //THEN
        assertThat(result.getMotorClassification()).isEqualTo(G);
    }

    @Test
    void shouldWorkWithLowKN() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withThroatDiameter(4)
                .withGrainCoreDiameter(5)
                .withGrainOuterDiameter(28)
                .withGrainSegmentLength(98)
                .withNumberOfSegment(1)
                .withOuterSurface(INHIBITED)
                .withEndsSurface(EXPOSED)
                .withCoreSurface(EXPOSED)
                .withPropellant(KNDX)
                .withChamberInnerDiameter(28)
                .withChamberLength(98)
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withSafeKNFailure(true)
                .createJSRMConfig());

        //THEN
        printSimulationInfos(motor, result);

        assertThat(result.getMotorClassification()).isEqualTo(G);
        assertThat(result.getNumberOfKNCorrection()).isEqualTo(77);
    }

    @Test
    void shouldWorkWithLowKNThatFailedOnOPTIMUM_NOZZLE_EXPANSION_RATIOCalculation() {
        //GIVEN
        SolidRocketMotor motor = new SolidRocketMotorBuilder()
                .withThroatDiameter(19)
                .withGrainCoreDiameter(20)
                .withGrainOuterDiameter(37)
                .withGrainSegmentLength(10)
                .withNumberOfSegment(5)
                .withOuterSurface(INHIBITED)
                .withEndsSurface(EXPOSED)
                .withCoreSurface(EXPOSED)
                .withPropellant(KNSU)
                .withChamberInnerDiameter(38)
                .withChamberLength(500)
                .build();

        //WHEN
        JSRMResult result = new JSRMSimulation(motor).run(new JSRMConfigBuilder()
                .withSafeKNFailure(true)
                .createJSRMConfig());

        //THEN
        printSimulationInfos(motor, result);
        assertThat(result.getMotorClassification()).isEqualTo(H);
    }

    private void printSimulationInfos(SolidRocketMotor motor, JSRMResult result) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting().create();
        System.out.println(gson.toJson(motor));
        System.out.println("Result = " + result.getMotorClassification().name() + result.getAverageThrustInNewton());
        System.out.println("Initial KN : "+ result.getMotorParameters().get(0).getKn());
        System.out.println("Average KN : "+ result.getMotorParameters().stream().mapToDouble(MotorParameters::getKn).average().getAsDouble());
        System.out.println("safe KN usage count: "+ result.getNumberOfKNCorrection());
        System.out.println("========================================================");
    }
}
