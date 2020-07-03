package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.infra.function.HollowCircleAreaFunction;
import com.github.jbgust.jsrm.infra.pressure.csv.CsvToDuringBurnPressureLine;
import com.github.jbgust.jsrm.utils.PropellantGrainBuilder;
import com.github.jbgust.jsrm.utils.SolidRocketMotorBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Map;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.infra.pressure.csv.DuringBurnPressureCsvLineAggregator.INTERVAL;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class HollowCylinderGrainTest {

    private int numberLineDuringBurnCalculation;
    private HollowCylinderGrain hollowCylinderGrain;
    private SolidRocketMotorBuilder solidRocketMotorBuilder;

    @BeforeEach
    void setUp() {
        numberLineDuringBurnCalculation = new JSRMConfigBuilder().createJSRMConfig().getNumberLineDuringBurnCalculation() -1;

        solidRocketMotorBuilder = new SolidRocketMotorBuilder();

        this.hollowCylinderGrain = new HollowCylinderGrain(
               solidRocketMotorBuilder.getGrainOuterDiameter(),
               solidRocketMotorBuilder.getGrainCoreDiameter(),
               solidRocketMotorBuilder.getGrainSegmentLength(),
               solidRocketMotorBuilder.getNumberOfSegment(),
               solidRocketMotorBuilder.getOuterSurface(),
               solidRocketMotorBuilder.getEndsSurface(),
               solidRocketMotorBuilder.getCoreSurface());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/SRM_2014_DURING_BURN_PRESSURE_QUALIFICATION.csv", numLinesToSkip = 1, delimiter = '|')
    @DisplayName("Check hollow cylinder grain implementation")
    void shouldComputeWithOuterInhibited(@CsvToDuringBurnPressureLine Map<String, Double> expectedLine) {
        double burnProgression = expectedLine.get(INTERVAL) / numberLineDuringBurnCalculation;

        assertThat(hollowCylinderGrain.getGrainVolume(burnProgression))
                .describedAs("Grain volume")
                .isCloseTo(expectedLine.get("GRAIN_VOLUME"), offset(1d));
    }

    @Test
    void shouldComputeWithAllExposed() {
        double tweb = solidRocketMotorBuilder.getGrainOuterDiameter() - solidRocketMotorBuilder.getGrainCoreDiameter();
        double webRegression = 0.5 * tweb / 2;
        HollowCylinderGrain grain = new HollowCylinderGrain(
                solidRocketMotorBuilder.getGrainOuterDiameter(),
                solidRocketMotorBuilder.getGrainCoreDiameter(),
                solidRocketMotorBuilder.getGrainSegmentLength(),
                solidRocketMotorBuilder.getNumberOfSegment(),
                EXPOSED, EXPOSED, EXPOSED);

        //0% burn progress
        assertThat(grain.getGrainOuterDiameter(0.0)).isEqualTo(solidRocketMotorBuilder.getGrainOuterDiameter());

        //50% burn progress
        double outerDiameterAtHalfBrun = solidRocketMotorBuilder.getGrainOuterDiameter() - webRegression;

        assertThat(grain.getGrainOuterDiameter(0.5)).isEqualTo(outerDiameterAtHalfBrun);

        assertThat(grain.getGrainEndSurface(0.5))
                .describedAs("Grain end surface 50%")
                .isCloseTo(new HollowCircleAreaFunction().runFunction(
                        outerDiameterAtHalfBrun,
                        solidRocketMotorBuilder.getGrainCoreDiameter() + webRegression
                ), offset(0.1d));

        assertThat(grain.getGrainVolume(0.5))
                .describedAs("Grain volume 50%")
                .isCloseTo(new HollowCircleAreaFunction().runFunction(
                        outerDiameterAtHalfBrun,
                        solidRocketMotorBuilder.getGrainCoreDiameter() + webRegression
                ) * solidRocketMotorBuilder.getNumberOfSegment() * (solidRocketMotorBuilder.getGrainSegmentLength() - webRegression), offset(0.1d));


        //100% burn progress
        assertThat(grain.getGrainEndSurface(1))
                .describedAs("Grain end surface 100%")
                .isEqualTo(0);

        assertThat(grain.getGrainVolume(1))
                .describedAs("Grain volume 100%")
                .isEqualTo(0.0);
    }

    @Test
    void shouldCheckSolidRocketMotor(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 8d);

        assertDoesNotThrow(() -> propellantGrain.getGrainConfigutation().checkConfiguration(solidRocketMotor));
    }

    @Test
    void shouldThrowExceptionIfCoreDiameterIsLessThanThroatDiameter(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withCoreDiameter(8)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 8.1d);

        assertThatThrownBy(() -> propellantGrain.getGrainConfigutation().checkConfiguration(solidRocketMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Throat diameter should be <= than grain core diameter");
    }

    @Test
    void shouldNotThrowExceptionIfCoreDiameterIsEqualToThroatDiameter(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withCoreDiameter(8)
                .build();

        propellantGrain.getGrainConfigutation().checkConfiguration(new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 8d));
    }

    @Test
    void shouldThrowExceptionIfCombustionChamberDiameterIsLessThanGrainOuterDiameter(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withOuterDiameter(20.1)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 5d);

        assertThatThrownBy(() -> propellantGrain.getGrainConfigutation().checkConfiguration(solidRocketMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combution chamber diameter should be >= than grain outer diameter");
    }

    @Test
    void shouldThrowExceptionIfGrainOuterDiameterIsLessThanCoreDiameter(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withOuterDiameter(20)
                .withCoreDiameter(20)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 5d);

        assertThatThrownBy(() -> propellantGrain.getGrainConfigutation().checkConfiguration(solidRocketMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Grain outer diameter should be > than grain core diameter");
    }

    @Test
    void shouldThrowExceptionIfGrainLengthIsGreaterThanCombustionChamberLength(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withNumberOfSegments(2)
                .withSegmentLength(45)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 89), 5d);

        assertThatThrownBy(() -> propellantGrain.getGrainConfigutation().checkConfiguration(solidRocketMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("Combustion chamber length should be >= than Grain total length");
    }

    @Test
    void shouldThrowExceptionIfCoreAndOuterSurfaceAreInhibited(){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withCoreSurface(INHIBITED)
                .withOuterSurface(INHIBITED)
                .build();

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(propellantGrain, new CombustionChamber(20, 80), 5d);

        assertThatThrownBy(() -> propellantGrain.getGrainConfigutation().checkConfiguration(solidRocketMotor))
                .isInstanceOf(InvalidMotorDesignException.class)
                .hasMessage("The motor should have at least core surface or outer surface exposed.");
    }

}
