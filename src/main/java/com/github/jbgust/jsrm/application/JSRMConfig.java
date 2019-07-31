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
    private final int numberLineDuringBurnCalculation;
    private final int numberLineDuringPostBurnCalculation;
    private boolean safeKNFailure;

    JSRMConfig(double densityRatio,
               double nozzleErosionInMillimeter,
               double combustionEfficiencyRatio,
               double ambiantPressureInMPa,
               double erosiveBurningAreaRatioThreshold,
               double erosiveBurningVelocityCoefficient,
               double nozzleEfficiency,
               boolean optimalNozzleDesign,
               Double nozzleExpansionRatio,
               int numberLineDuringBurnCalculation,
               int numberLineDuringPostBurnCalculation,
               boolean safeKNFailure) {

        this.densityRatio = densityRatio;
        this.nozzleErosionInMillimeter = nozzleErosionInMillimeter;
        this.combustionEfficiencyRatio = combustionEfficiencyRatio;
        this.ambiantPressureInMPa = ambiantPressureInMPa;
        this.erosiveBurningAreaRatioThreshold = erosiveBurningAreaRatioThreshold;
        this.erosiveBurningVelocityCoefficient = erosiveBurningVelocityCoefficient;
        this.nozzleEfficiency = nozzleEfficiency;
        this.optimalNozzleDesign = optimalNozzleDesign;
        this.nozzleExpansionRatio = nozzleExpansionRatio;
        this.numberLineDuringBurnCalculation = numberLineDuringBurnCalculation;
        this.numberLineDuringPostBurnCalculation = numberLineDuringPostBurnCalculation;
        this.safeKNFailure = safeKNFailure;
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

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public int getNumberLineDuringBurnCalculation() {
        return numberLineDuringBurnCalculation;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public int getNumberLineDuringPostBurnCalculation() {
        return numberLineDuringPostBurnCalculation;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public int getLastCalcultationLine() {
        return numberLineDuringBurnCalculation + numberLineDuringPostBurnCalculation;
    }

    /**
     * See builder documentation {@link JSRMConfigBuilder}
     * @return See builder documentation {@link JSRMConfigBuilder}
     */
    public boolean isSafeKNFailure() {
        return safeKNFailure;
    }
}
