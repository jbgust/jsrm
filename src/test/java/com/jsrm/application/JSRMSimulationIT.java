package com.jsrm.application;

import com.jsrm.application.csv.CsvToThrustResult;
import com.jsrm.application.motor.MotorChamber;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.application.motor.propellant.PropellantGrain;
import com.jsrm.application.result.JSRMResult;
import com.jsrm.application.result.Nozzle;
import com.jsrm.application.result.ThrustResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static com.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.jsrm.application.result.MotorClassification.L;
import static com.jsrm.infra.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@DisplayName("JSRM Integration test")
class JSRMSimulationIT {

    private static JSRMResult jsrmResult;
    private static int lineToAssert = 0;

    @BeforeAll
    static void shouldRunJSRMSimulation() {
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, 69d, 20d,
                115d, 4d,
                INHIBITED, EXPOSED, EXPOSED);
        MotorChamber motorChamber = new MotorChamber(75d, 470d);

        double throatDiameter = 17.3985248919802;

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, motorChamber,
                6d, throatDiameter, 0d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(solidRocketMotor);
        //see SRM_2014.xls
        JSRMConfig jsrmConfig = new JSRMConfig.Builder()
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
                    .isEqualTo(L);

            assertThat(jsrmResult.getMaxChamberPressureInMPa())
                    .describedAs("Max chamber pressure")
                    .isEqualTo(5.93, offset(0.01d));

            assertThat(jsrmResult.getMaxThrustInNewton())
                    .describedAs("Max thrust")
                    .isEqualTo(2060, offset(1d));

            assertThat(jsrmResult.getTotalImpulseInNewtonSecond())
                    .describedAs("Total impluse")
                    .isEqualTo(3602,  offset(1d));

            assertThat(jsrmResult.getSpecificImpulseInSecond())
                    .describedAs("Specific impulse")
                    .isEqualTo(130.6, offset(0.1d));

            assertThat(jsrmResult.getSpecificImpulseInSecond())
                    .describedAs("Specific impulse")
                    .isEqualTo(130.6, offset(0.1d));

            assertThat(jsrmResult.getThrustTimeInSecond())
                    .describedAs("Thrust time")
                    .isEqualTo(2.1575, offset(0.0001d));

            assertThat(jsrmResult.getAverageThrustInNewton())
                    .describedAs("Average thrust")
                    .isEqualTo(1670);
        }

        @Test
        @DisplayName("Nozzle configuration")
        void checkNozzleResults() {

            // TODO: Assert Nozzle (diemnsion de la tuyère en fonction des angles alpha et beta
            Nozzle nozzle = jsrmResult.getNozzle();

            assertThat(nozzle.getOptimalNozzleExpansionRatio())
                    .describedAs("Optimal nozzle expansion ratio")
                    .isEqualTo(9.633, offset(0.001d));
            assertThat(nozzle.getNozzleExpansionRatio())
                    .describedAs("nozzle expansion ratio")
                    .isEqualTo(8);

            assertThat(nozzle.getInitialNozzleExitSpeedInMach())
                    .describedAs("Mach No. at nozzle exit (initial)")
                    .isEqualTo(2.955, offset(0.001d));

            assertThat(nozzle.getFinalNozzleExitSpeedInMach())
                    .describedAs("Mach No. at nozzle exit (final)")
                    .isEqualTo(2.955, offset(0.001d));
        }

        @ParameterizedTest
        @DisplayName("Thrust by time")
        @CsvFileSource(resources = "/SRM_2014_THRUST_BY_TIME.csv", numLinesToSkip = 2, delimiter = '|')
        void checkThrustDataResults(@CsvToThrustResult ThrustResult expectedThrustResult) {

            ThrustResult thrustResult = jsrmResult.getThrustResults().get(lineToAssert++);

            assertThat(thrustResult.getTimeSinceBurnStartInSecond())
                    .isEqualTo(expectedThrustResult.getTimeSinceBurnStartInSecond(), offset(0.0001d));

            assertThat(thrustResult.getThrustInNewton())
                    .isEqualTo(expectedThrustResult.getThrustInNewton(), offset(1d));

        }

    }

    @Test
    @DisplayName("Optimal nozzle design")
    void shoulduseOptimalNozzleDesing(){

        // GIVEN
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, 69d, 20d,
                115d, 4d,
                INHIBITED, EXPOSED, EXPOSED);
        MotorChamber motorChamber = new MotorChamber(75d, 470d);

        double throatDiameter = 17.3985248919802;

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, motorChamber,
                6d, throatDiameter, 0d);

        JSRMSimulation jsrmSimulation = new JSRMSimulation(solidRocketMotor);
        //see SRM_2014.xls
        JSRMConfig config = new JSRMConfig
                .Builder()
                .createJSRMConfig();

        // WHEN
        JSRMResult result = jsrmSimulation.run(config);

        //THEN
        assertThat(config.isOptimalNozzleDesign()).isTrue();

        Nozzle nozzle = result.getNozzle();

        assertThat(nozzle.getNozzleExpansionRatio())
                .isEqualTo(9.632509, offset(0.0001d));

        assertThat(nozzle.getInitialNozzleExitSpeedInMach())
                .isEqualTo(3.065, offset(0.001d));

        assertThat(nozzle.getFinalNozzleExitSpeedInMach())
                .isEqualTo(3.065, offset(0.001d));
    }
}