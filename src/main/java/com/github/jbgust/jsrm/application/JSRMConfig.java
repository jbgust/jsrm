package com.github.jbgust.jsrm.application;

public class JSRMConfig {

    private final double densityRatio;
    private final double nozzleErosionInMillimeter;
    private final double combustionEfficiencyRatio;
    private final double ambiantPressureInMPa;
    private final double erosiveBurningAreaRatioThreshold;
    private final double erosiveBurningVelocityCoefficient;
    private final double nozzleEfficiency;
    private final boolean optimalNozzleDesign;
    private final Double nozzleExpansionRatio;

    JSRMConfig(double densityRatio,
                       double nozzleErosionInMillimeter,
                       double combustionEfficiencyRatio,
                       double ambiantPressureInMPa,
                       double erosiveBurningAreaRatioThreshold,
                       double erosiveBurningVelocityCoefficient,
                       double nozzleEfficiency,
                       boolean optimalNozzleDesign,
                       Double nozzleExpansionRatio) {

        this.densityRatio = densityRatio;
        this.nozzleErosionInMillimeter = nozzleErosionInMillimeter;
        this.combustionEfficiencyRatio = combustionEfficiencyRatio;
        this.ambiantPressureInMPa = ambiantPressureInMPa;
        this.erosiveBurningAreaRatioThreshold = erosiveBurningAreaRatioThreshold;
        this.erosiveBurningVelocityCoefficient = erosiveBurningVelocityCoefficient;
        this.nozzleEfficiency = nozzleEfficiency;
        this.optimalNozzleDesign = optimalNozzleDesign;
        this.nozzleExpansionRatio = nozzleExpansionRatio;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public double getDensityRatio() {
        return densityRatio;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public double getNozzleErosionInMillimeter() {
        return nozzleErosionInMillimeter;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public double getCombustionEfficiencyRatio() {
        return combustionEfficiencyRatio;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public double getAmbiantPressureInMPa() {
        return ambiantPressureInMPa;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public double getErosiveBurningAreaRatioThreshold() {
        return erosiveBurningAreaRatioThreshold;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public double getErosiveBurningVelocityCoefficient() {
        return erosiveBurningVelocityCoefficient;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public double getNozzleEfficiency() {
        return nozzleEfficiency;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public boolean isOptimalNozzleDesign() {
        return optimalNozzleDesign;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public Double getNozzleExpansionRatio() {
        return nozzleExpansionRatio;
    }

}
