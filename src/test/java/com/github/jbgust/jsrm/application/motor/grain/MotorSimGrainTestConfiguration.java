package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import org.junit.jupiter.api.BeforeEach;

public class MotorSimGrainTestConfiguration {

    protected JSRMConfig motorSimConfig;

    @BeforeEach
    public void setUp() {
        motorSimConfig = new JSRMConfigBuilder()
                .withCombustionEfficiencyRatio(0.97)
                .withDensityRatio(.96)
                .withNozzleExpansionRatio(8)
                .createJSRMConfig();
    }

}
