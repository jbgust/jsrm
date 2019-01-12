package com.jsrm.infra;

import com.jsrm.application.JSRMConfig;
import com.jsrm.application.motor.SolidRocketMotor;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.jsrm.application.JSRMSimulationIT.createMotorAsSRM_2014ExcelFile;
import static com.jsrm.infra.JSRMConstant.*;
import static com.jsrm.infra.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class ConstantsExtractorTest {

    @Test
    void shouldExtractConstants(){
        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();
        JSRMConfig config = new JSRMConfig.Builder().createJSRMConfig();

        Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(solidRocketMotor, config, KNDX.getId());

        assertThat(constants.get(two)).isEqualTo(24.5);

        assertThat(constants.get(xincp)).isEqualTo(0.0293764988009578, offset(0.00000000000001d));
        assertThat(constants.get(cstar)).isEqualTo(889.279521360202, offset(0.000000000001));
        assertThat(constants.get(mgrain)).isEqualTo(2.812445952, offset(0.000000001));

        assertThat(constants.get(patm)).isEqualTo(config.getAmbiantPressureInMPa());
        assertThat(constants.get(etanoz)).isEqualTo(config.getNozzleEfficiency());
        assertThat(constants.get(k2ph)).isEqualTo(KNDX.getK2Ph());

        //TODO assert
    }

    //TODO Test extraction
    //TODO test sir les inhibited and exposed
}