package com.github.jbgust.jsrm.application;

import com.github.jbgust.jsrm.application.exception.InvalidConfigurationException;

/**
 * This class should be use to create a JSRMConfig
 */
public class JSRMConfigBuilder {

    private double densityRatio = 0.95;
    private double nozzleErosionInMillimeter = 0.0;
    private double combustionEfficiencyRatio = 0.95;
    private double ambiantPressureInMPa = 0.101;
    private double erosiveBurningAreaRatioThreshold = 6.0;
    private double erosiveBurningVelocityCoefficient = 0;
    private double nozzleEfficiency = 0.85;
    private boolean optimalNozzleDesign = true;
    private Double nozzleExpansionRatio = null;

    private int numberLineDuringBurnCalculation = 835;
    private int numberLineDuringPostBurnCalculation = 47;

    /**
     * Change density ratio (Grain actual density / Grain ideal density)
     * @param densityRatio typically 0.90 to 0.98 (default 0.95)
     * @return the builder
     */
    public JSRMConfigBuilder withDensityRatio(double densityRatio) {
        this.densityRatio = densityRatio;
        return this;
    }

    /**
     * Change increase in throat diameter due to erosion
     * @param nozzleErosionInMillimeter  in millimeter (default 0.0mm)
     * @return the builder
     */
    public JSRMConfigBuilder withNozzleErosionInMillimeter(double nozzleErosionInMillimeter) {
        this.nozzleErosionInMillimeter = nozzleErosionInMillimeter;
        return this;
    }

    /**
     * Change combustion efficiency ration
     * @param combustionEfficiencyRatio Use 0.95 for fine grain propellant. Use 0.93 for coarse grain propellant (default 0.95)
     * @return the builder
     */
    public JSRMConfigBuilder withCombustionEfficiencyRatio(double combustionEfficiencyRatio) {
        this.combustionEfficiencyRatio = combustionEfficiencyRatio;
        return this;
    }

    /**
     * Change local barometric pressure.
     * @param ambiantPressureInMPa typically 0.101 MPa at sea level (default 0.101Mpa)
     * @return the builder
     */
    public JSRMConfigBuilder withAmbiantPressureInMPa(double ambiantPressureInMPa) {
        this.ambiantPressureInMPa = ambiantPressureInMPa;
        return this;
    }

    /**
     * Change ratio of core to throat cross-sectional areas, above which no erosive burning occurs
     * @param erosiveBurningAreaRatioThreshold (default 6.0)
     * @return the builder
     */
    public JSRMConfigBuilder withErosiveBurningAreaRatioThreshold(double erosiveBurningAreaRatioThreshold) {
        this.erosiveBurningAreaRatioThreshold = erosiveBurningAreaRatioThreshold;
        return this;
    }

    /**
     * An empirical constant. Enter zero if no erosive burning expected.
     * @param erosiveBurningVelocityCoefficient Typically 0.0 to 1.0 (default 0.0)
     * @return the builder
     */
    public JSRMConfigBuilder withErosiveBurningVelocityCoefficient(double erosiveBurningVelocityCoefficient) {
        this.erosiveBurningVelocityCoefficient = erosiveBurningVelocityCoefficient;
        return this;
    }

    /**
     * Change nozzle efficiency
     * @param nozzleEfficiency For a well-contoured, smooth nozzle, typically 0.75 to 0.85 (default 0.85)
     * @return the builder
     */
    public JSRMConfigBuilder withNozzleEfficiency(double nozzleEfficiency) {
        this.nozzleEfficiency = nozzleEfficiency;
        return this;
    }

    /**
     * Use automatic calculation to obtain the optimum nozzle expansion ratio.
     * @param optimalNozzleDesign If set to false, you have to set nozzle expansion ratio with .withNozzleExpansionRatio()
     * @return the builder
     */
    public JSRMConfigBuilder withOptimalNozzleDesign(boolean optimalNozzleDesign) {
        this.optimalNozzleDesign = optimalNozzleDesign;
        return this;
    }

    /**
     * Use to specify more or less line for calculation
     * @param numberOfCalculationLine default value is 882 (same as SRM Excel file)
     * @return the builder
     */
    public JSRMConfigBuilder withNumberOfCalculationLine(int numberOfCalculationLine) {
        this.numberLineDuringBurnCalculation = (int) (numberOfCalculationLine*0.96);
        this.numberLineDuringPostBurnCalculation = numberOfCalculationLine - this.numberLineDuringBurnCalculation - 1;
        return this;
    }

    /**
     * Change ratio of cross-sectional areas of nozzle exit  to throat.
     * Should be set if optimalNozzleDesign is false
     * @param nozzleExpansionRatio the expansion ratio
     * @return the builder
     */
    public JSRMConfigBuilder withNozzleExpansionRatio(double nozzleExpansionRatio) {
        this.nozzleExpansionRatio = nozzleExpansionRatio;
        this.optimalNozzleDesign = false;
        return this;
    }

    /**
     * Build the configuration
     * @return the config
     */
    public JSRMConfig createJSRMConfig() {
        if(!optimalNozzleDesign && nozzleExpansionRatio==null){
            throw new InvalidConfigurationException("Your configuration should defined a nozzleExpansionRatio if you don't use optimalNozzleDesign");
        }

        if(optimalNozzleDesign && nozzleExpansionRatio!=null){
            throw new InvalidConfigurationException("Your configuration should not use both optimalNozzleDesign and nozzleExpansionRatio");
        }

        return new JSRMConfig(
                densityRatio,
                nozzleErosionInMillimeter,
                combustionEfficiencyRatio,
                ambiantPressureInMPa,
                erosiveBurningAreaRatioThreshold,
                erosiveBurningVelocityCoefficient,
                nozzleEfficiency,
                optimalNozzleDesign,
                nozzleExpansionRatio,
                numberLineDuringBurnCalculation,
                numberLineDuringPostBurnCalculation);
    }
}
