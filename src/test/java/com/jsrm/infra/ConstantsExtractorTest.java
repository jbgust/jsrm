package com.jsrm.infra;

import com.jsrm.application.JSRMConfig;
import com.jsrm.application.JSRMConfigBuilder;
import com.jsrm.application.motor.CombustionChamber;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.application.motor.propellant.GrainSurface;
import com.jsrm.application.motor.propellant.PropellantGrain;
import com.jsrm.application.motor.propellant.SolidPropellant;
import com.jsrm.application.motor.utils.PropellantGrainBuilder;
import com.jsrm.application.motor.propellant.PropellantType;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static com.jsrm.application.JSRMSimulationIT.createMotorAsSRM_2014ExcelFile;
import static com.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.jsrm.infra.JSRMConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class ConstantsExtractorTest {

    @Test
    void shouldExtractConstants(){
        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();
        JSRMConfig config = new JSRMConfigBuilder().createJSRMConfig();

        Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(solidRocketMotor, config);

        assertThat(constants.size()).isEqualTo(numberOfConstantThatShouldBeInitialized());

        assertThat(constants.get(two)).isEqualTo(24.5);
        assertThat(constants.get(xincp)).isEqualTo(0.0293764988009578, offset(0.00000000000001d));
        assertThat(constants.get(cstar)).isEqualTo(889.279521360202, offset(0.000000000001));
        assertThat(constants.get(patm)).isEqualTo(config.getAmbiantPressureInMPa());
        assertThat(constants.get(etanoz)).isEqualTo(config.getNozzleEfficiency());
        assertThat(constants.get(ci)).isEqualTo(EXPOSED.value());
        assertThat(constants.get(osi)).isEqualTo(INHIBITED.value());
        assertThat(constants.get(ei)).isEqualTo(EXPOSED.value());
        assertThat(constants.get(at)).isEqualTo(237.746832, offset(0.000001));

        assertThat(constants.get(n)).isEqualTo(4);
        assertThat(constants.get(dto)).isEqualTo(solidRocketMotor.getThroatDiameterInMillimeter());
        assertThat(constants.get(erate)).isEqualTo(config.getNozzleErosionInMillimeter());

        CombustionChamber combustionChamber = solidRocketMotor.getCombustionChamber();
        assertThat(constants.get(dc)).isEqualTo(combustionChamber.getChamberInnerDiameterInMillimeter());
        assertThat(constants.get(vc)).isEqualTo(combustionChamber.getVolume());

        assertThat(constants.get(gstar)).isEqualTo(config.getErosiveBurningAreaRatioThreshold());
        assertThat(constants.get(kv)).isEqualTo(config.getErosiveBurningVelocityCoefficient());
        assertThat(constants.get(pbd)).isEqualTo(PBD);

        SolidPropellant propellant = solidRocketMotor.getPropellantGrain().getPropellant();
        assertThat(constants.get(propellantId)).isEqualTo(((PropellantType)propellant).getId());
        assertThat(constants.get(k2ph)).isEqualTo(propellant.getK2Ph());
        assertThat(constants.get(rat)).isEqualTo(UNIVERSAL_GAS_CONSTANT / propellant.getEffectiveMolecularWeight());
        assertThat(constants.get(to)).isEqualTo(config.getCombustionEfficiencyRatio() * propellant.getChamberTemperature());
        assertThat(constants.get(k)).isEqualTo(propellant.getK());

        assertThat(constants.get(mgrain)).isEqualTo(2.812445952, offset(0.000000001));
        assertThat(constants.get(rhopgrain)).isEqualTo(config.getDensityRatio()*propellant.getIdealMassDensity());
    }

    @ParameterizedTest
    @MethodSource("grainSurfaceAssertValues")
    void shouldConvertGrainSurfacesToConstants(GrainSurfaceAssertValue value){
        PropellantGrain propellantGrain = new PropellantGrainBuilder()
                .withCoreSurface(value.getCoreSurface())
                .withEndsSurface(value.getEndSurface())
                .withOuterSurface(value.getOuterSurface())
                .build();

        SolidRocketMotor motor = new SolidRocketMotor(propellantGrain, new CombustionChamber(1, 1), 1.0);

        Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(motor, new JSRMConfigBuilder().createJSRMConfig());

        assertThat(constants.get(osi)).isEqualTo(value.getOsi());
        assertThat(constants.get(ci)).isEqualTo(value.getCi());
        assertThat(constants.get(ei)).isEqualTo(value.getEi());
    }

    static Stream<GrainSurfaceAssertValue> grainSurfaceAssertValues() {
        return Stream.of(
                new GrainSurfaceAssertValue(INHIBITED, 0, INHIBITED, 0, INHIBITED, 0),
                new GrainSurfaceAssertValue(EXPOSED, 1, INHIBITED, 0, INHIBITED, 0),
                new GrainSurfaceAssertValue(INHIBITED, 0, EXPOSED, 1, INHIBITED, 0),
                new GrainSurfaceAssertValue(INHIBITED, 0, INHIBITED, 0, EXPOSED, 1),
                new GrainSurfaceAssertValue(EXPOSED, 1, EXPOSED, 1, EXPOSED, 1)
        );
    }

    @Value
    private static class GrainSurfaceAssertValue{
        GrainSurface coreSurface;
        int ci;

        GrainSurface outerSurface;
        int osi;

        GrainSurface endSurface;
        int ei;
    }

    private long numberOfConstantThatShouldBeInitialized(){
        return Stream.of(JSRMConstant.values())
                .filter(constant -> !constant.isConstantExtractedDuringCalculation())
                .count();
    }
}