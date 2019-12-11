package com.github.jbgust.jsrm.infra;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.utils.SolidRocketMotorBuilder;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.infra.JSRMConstant.*;
import static com.github.jbgust.jsrm.utils.SolidRocketMotorBuilder.createMotorAsSRM_2014ExcelFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class ConstantsExtractorTest {

    private final JSRMConfig config = new JSRMConfigBuilder().createJSRMConfig();

    @Test
    void shouldExtractConstants(){
        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();

        Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(solidRocketMotor, config);

        assertThat(constants.size()).isEqualTo(numberOfConstantThatShouldBeInitialized());

        assertThat(constants.get(xincp)).isEqualTo(0.0293764988009578, offset(0.00000000000001d));
        assertThat(constants.get(cstar)).isEqualTo(889.279521360202, offset(0.000000000001));
        assertThat(constants.get(patm)).isEqualTo(config.getAmbiantPressureInMPa());
        assertThat(constants.get(etanoz)).isEqualTo(config.getNozzleEfficiency());
        assertThat(constants.get(at)).isEqualTo(237.746832, offset(0.000001));

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

    @Test
    void shouldComputeXINCIfOuterAndCoreAreExposed(){
        SolidRocketMotor solidRocketMotor = new SolidRocketMotorBuilder()
                .withOuterSurface(EXPOSED)
                .withCoreSurface(EXPOSED)
                .build();

        Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(solidRocketMotor, config);

        assertThat(constants.get(xincp)).isEqualTo(0.0146882494004792, offset(0.00000000000001d));
    }


    private long numberOfConstantThatShouldBeInitialized(){
        return Stream.of(JSRMConstant.values())
                .filter(constant -> !constant.isConstantExtractedDuringCalculation())
                .count();
    }
}
