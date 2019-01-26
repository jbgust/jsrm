package com.jsrm.application;

import com.jsrm.application.exception.InvalidConfigurationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JSRMConfigBuilderTest {

    @Test
    void shouldThrowExceptionIfConfigIsInvalid() {

        assertThatThrownBy(() -> new JSRMConfigBuilder()
                .withOptimalNozzleDesign(false)
                .createJSRMConfig())
                .isInstanceOf(InvalidConfigurationException.class)
                .hasMessage("Your configuration should defined a nozzleExpansionRatio is you don't use optimalNozzleDesign");

        assertThatThrownBy(() -> new JSRMConfigBuilder()
                .withNozzleExpansionRatio(3)
                .withOptimalNozzleDesign(true)
                .createJSRMConfig())
                .isInstanceOf(InvalidConfigurationException.class)
                .hasMessage("Your configuration should not use both optimalNozzleDesign and nozzleExpansionRatio");
    }

    @Test
    void shouldDisableOptimalNozzleDesignIfNozzleExpansionRatioIsSet() {
        JSRMConfig jsrmConfig = new JSRMConfigBuilder()
                .withOptimalNozzleDesign(true)
                .withNozzleExpansionRatio(3)
                .createJSRMConfig();

        assertThat(jsrmConfig.getNozzleExpansionRatio()).isEqualTo(3);
        assertThat(jsrmConfig.isOptimalNozzleDesign()).isFalse();
    }

    @Test
    void shouldBuildConfiguration() {
        JSRMConfig jsrmConfig = new JSRMConfigBuilder()
                .withNozzleExpansionRatio(3)
                .withAmbiantPressureInMPa(4)
                .withCombustionEfficiencyRatio(5)
                .withDensityRatio(6)
                .withErosiveBurningAreaRatioThreshold(7)
                .withErosiveBurningVelocityCoefficient(8)
                .withNozzleEfficiency(9)
                .withNozzleErosionInMillimeter(10)
                .createJSRMConfig();

        assertThat(jsrmConfig.getNozzleExpansionRatio()).isEqualTo(3);
        assertThat(jsrmConfig.isOptimalNozzleDesign()).isFalse();
        assertThat(jsrmConfig.getAmbiantPressureInMPa()).isEqualTo(4);
        assertThat(jsrmConfig.getCombustionEfficiencyRatio()).isEqualTo(5);
        assertThat(jsrmConfig.getDensityRatio()).isEqualTo(6);
        assertThat(jsrmConfig.getErosiveBurningAreaRatioThreshold()).isEqualTo(7);
        assertThat(jsrmConfig.getErosiveBurningVelocityCoefficient()).isEqualTo(8);
        assertThat(jsrmConfig.getNozzleEfficiency()).isEqualTo(9);
        assertThat(jsrmConfig.getNozzleErosionInMillimeter()).isEqualTo(10);
    }

    @Test
    void shouldBuildConfigurationWithDefaultValue() {
        JSRMConfig jsrmConfig = new JSRMConfigBuilder().createJSRMConfig();

        assertThat(jsrmConfig.getAmbiantPressureInMPa()).isEqualTo(0.101);
        assertThat(jsrmConfig.getCombustionEfficiencyRatio()).isEqualTo(0.95);
        assertThat(jsrmConfig.getDensityRatio()).isEqualTo(0.95);
        assertThat(jsrmConfig.getErosiveBurningAreaRatioThreshold()).isEqualTo(6);
        assertThat(jsrmConfig.getErosiveBurningVelocityCoefficient()).isEqualTo(0);
        assertThat(jsrmConfig.getNozzleEfficiency()).isEqualTo(0.85);
        assertThat(jsrmConfig.getNozzleErosionInMillimeter()).isEqualTo(0);
        assertThat(jsrmConfig.isOptimalNozzleDesign()).isTrue();
        assertThat(jsrmConfig.getNozzleExpansionRatio()).isNull();
    }
}