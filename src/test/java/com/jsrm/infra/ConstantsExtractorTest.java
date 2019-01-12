package com.jsrm.infra;

import com.jsrm.application.JSRMConfig;
import com.jsrm.application.motor.SolidRocketMotor;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.jsrm.application.JSRMSimulationIT.createMotorAsSRM_2014ExcelFile;
import static com.jsrm.infra.JSRMConstant.two;
import static com.jsrm.infra.JSRMConstant.xincp;
import static com.jsrm.infra.propellant.PropellantType.KNDX;
import static org.assertj.core.api.Assertions.assertThat;

class ConstantsExtractorTest {

    @Test
    void shouldExtractConstants(){
        SolidRocketMotor solidRocketMotor = createMotorAsSRM_2014ExcelFile();
        JSRMConfig config = new JSRMConfig.Builder().createJSRMConfig();

        Map<JSRMConstant, Double> constants = ConstantsExtractor.extract(solidRocketMotor, config, KNDX.getId());

        assertThat(constants.get(two)).isEqualTo(24.5);

        assertThat(constants.get(xincp)).isEqualTo(0.0293764988009578, Offset.offset(0.00000000000001d));
    }
}