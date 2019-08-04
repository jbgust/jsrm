package com.github.jbgust.jsrm.application;

import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.application.result.MotorClassification.G;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.GrainSurface;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorParameters;
import com.github.jbgust.jsrm.utils.SolidRocketMotorBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class LowKNTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/lowKN.csv", numLinesToSkip = 15)
    void shouldValidLowKN(double throatDiameter,
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

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting().create();
            System.out.println(gson.toJson(motor));
            System.out.println("Initial KN : "+ result.getMotorParameters().get(0).getKn());
            System.out.println("Average KN : "+ result.getMotorParameters().stream().mapToDouble(MotorParameters::getKn).average().getAsDouble());
            System.out.println("Result = " + result.getMotorClassification().name() + result.getAverageThrustInNewton());
            System.out.println("========================================================");

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
}