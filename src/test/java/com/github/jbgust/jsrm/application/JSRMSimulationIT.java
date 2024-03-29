package com.github.jbgust.jsrm.application;

import com.github.jbgust.jsrm.application.csv.CsvToThrustResult;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorClassification;
import com.github.jbgust.jsrm.application.result.MotorParameters;
import com.github.jbgust.jsrm.application.result.Nozzle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static com.github.jbgust.jsrm.utils.SolidRocketMotorBuilder.createMotorAsSRM_2014ExcelFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@DisplayName("JSRM Integration test")
public class JSRMSimulationIT {

    private static JSRMResult jsrmResult;
    private static int lineToAssert = 0;

    @BeforeAll
    static void shouldRunJSRMSimulation() {
        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();

        JSRMSimulation jsrmSimulation = new JSRMSimulation(solidRocketMotor);
        //see SRM_2014.xls
        JSRMConfig jsrmConfig = new JSRMConfigBuilder()
                .withNozzleExpansionRatio(8)
                .createJSRMConfig();

        jsrmResult = jsrmSimulation.run(jsrmConfig);
    }

    @Nested
    @DisplayName("Check SRM_2014.xls results")
    class CheckingResults {

        @Test
        @DisplayName("Motor performance")
        void checkPerformanceResults() {
            assertThat(jsrmResult.getMotorClassification())
                    .describedAs("Motor classification")
                    .isEqualTo(MotorClassification.L);

            assertThat(jsrmResult.getMaxChamberPressureInMPa())
                    .describedAs("Max chamber pressure")
                    .isEqualTo(5.93, offset(0.01));

            assertThat(jsrmResult.getAverageChamberPressureInMPa())
                    .describedAs("Average chamber pressure")
                    .isEqualTo(4.89, offset(0.01));

            assertThat(jsrmResult.getMaxThrustInNewton())
                    .describedAs("Max thrust")
                    .isEqualTo(2060, offset(1d));

            assertThat(jsrmResult.getTotalImpulseInNewtonSecond())
                    .describedAs("Total impluse")
                    .isEqualTo(3602,  offset(1d));

            assertThat(jsrmResult.getSpecificImpulseInSecond())
                    .describedAs("Specific impulse")
                    .isEqualTo(130.6, offset(0.1));

            assertThat(jsrmResult.getSpecificImpulseInSecond())
                    .describedAs("Specific impulse")
                    .isEqualTo(130.6, offset(0.1));

            assertThat(jsrmResult.getThrustTimeInSecond())
                    .describedAs("Thrust time")
                    .isEqualTo(2.1575, offset(0.0001));

            assertThat(jsrmResult.getAverageThrustInNewton())
                    .describedAs("Average thrust")
                    .isEqualTo(1670);
        }

        @Test
        @DisplayName("Nozzle configuration")
        void checkNozzleResults() {

            Nozzle nozzle = jsrmResult.getNozzle();

            assertThat(nozzle.getOptimalNozzleExpansionRatio())
                    .describedAs("Optimal nozzle expansion ratio")
                    .isEqualTo(9.633, offset(0.001));
            assertThat(nozzle.getNozzleExpansionRatio())
                    .describedAs("nozzle expansion ratio")
                    .isEqualTo(8);

            assertThat(nozzle.getNozzleExitDiameterInMillimeter())
                    .describedAs("Nozzle exit diameter")
                    .isEqualTo(49.21, offset(0.01));

            assertThat(nozzle.getOptimalNozzleExitDiameterInMillimeter())
                    .describedAs("Optimal nozzle exit diameter")
                    .isEqualTo(54, offset(0.01));

            assertThat(nozzle.getInitialNozzleExitSpeedInMach())
                    .describedAs("Mach No. at nozzle exit (initial)")
                    .isEqualTo(2.955, offset(0.001));

            assertThat(nozzle.getFinalNozzleExitSpeedInMach())
                    .describedAs("Mach No. at nozzle exit (final)")
                    .isEqualTo(2.955, offset(0.001));

            assertThat(nozzle.getConvergenceLenghtInMillimeter(35))
                    .describedAs("Convergence length")
                    .isEqualTo(41.13, offset(0.01));

            assertThat(nozzle.getDivergenceLenghtInMillimeter(12))
                    .describedAs("Divergence length")
                    .isEqualTo(74.83, offset(0.01));

            assertThat(nozzle.getOptimalDivergenceLenghtInMillimeter(12))
                    .describedAs("Optimal divergence length")
                    .isEqualTo(86.10, offset(0.01));
        }

        @ParameterizedTest
        @DisplayName("Thrust by time")
        @CsvFileSource(resources = "/SRM_2014_PERFORMANCES_BY_TIME.csv", numLinesToSkip = 2, delimiter = '|')
        void checkThrustDataResults(@CsvToThrustResult MotorParameters expectedMotorParameters) {

            MotorParameters motorParameters = jsrmResult.getMotorParameters().get(lineToAssert++);

            assertThat(motorParameters.getTimeSinceBurnStartInSecond())
                    .describedAs("Time")
                    .isEqualTo(expectedMotorParameters.getTimeSinceBurnStartInSecond(), offset(0.0001));

            assertThat(motorParameters.getThrustInNewton())
                    .describedAs("Thrust")
                    .isEqualTo(expectedMotorParameters.getThrustInNewton(), offset(1d));

            assertThat(motorParameters.getGrainMassInKg())
                    .describedAs("Grain mass")
                    .isEqualTo(expectedMotorParameters.getGrainMassInKg(), offset(1d));

            assertThat(motorParameters.getKn())
                    .describedAs("KN")
                    .isEqualTo(expectedMotorParameters.getKn(), offset(0.01d));

            assertThat(motorParameters.getChamberPressureInMPa())
                    .describedAs("Pressure")
                    .isEqualTo(expectedMotorParameters.getChamberPressureInMPa(), offset(0.001d));

            assertThat(motorParameters.getMassFlowRateInKgPerSec())
                    .describedAs("Mass flow rate")
                    .isEqualTo(expectedMotorParameters.getMassFlowRateInKgPerSec(), offset(0.0001d));
        }

    }

    @Test
    @DisplayName("Optimal nozzle design")
    void shoulduseOptimalNozzleDesing(){
        // GIVEN
        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();

        JSRMSimulation jsrmSimulation = new JSRMSimulation(solidRocketMotor);

        // WHEN
        JSRMResult result = jsrmSimulation.run();

        //THEN
        Nozzle nozzle = result.getNozzle();

        assertThat(nozzle.getNozzleExpansionRatio())
                .isEqualTo(9.632509, offset(0.0001d));

        assertThat(nozzle.getInitialNozzleExitSpeedInMach())
                .isEqualTo(3.065, offset(0.001d));

        assertThat(nozzle.getFinalNozzleExitSpeedInMach())
                .isEqualTo(3.065, offset(0.001d));
    }

    @Test
    @DisplayName("Other results check")
    void checkOtherResults(){
        assertThat(jsrmResult.getNumberOfKNCorrection())
                .describedAs("Number of correction due to low KN")
                .isEqualTo(0);

        assertThat(jsrmResult.getGrainMassInKg())
                .describedAs("Grain mass in Kg")
                .isCloseTo(2.812, offset(0.001));
    }
}
