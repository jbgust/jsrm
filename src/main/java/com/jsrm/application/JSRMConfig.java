package com.jsrm.application;

public class JSRMConfig {

    private final double densityRatio;
    private final double nozzleErosionInMillimeter;
    private final double combustionEfficiencyRatio;
    private final double ambiantPressureInMPa;
    private final double erosiveBurningAreaRatioThreshold;
    private final double erosiveBurningVelocityCoefficient;
    private final double nozzleEfficiency;
    private final boolean optimalNozzleDesign;
    private final double nozzleExpansionRatio;

    private JSRMConfig(double densityRatio,
                       double nozzleErosionInMillimeter,
                       double combustionEfficiencyRatio,
                       double ambiantPressureInMPa,
                       double erosiveBurningAreaRatioThreshold,
                       double erosiveBurningVelocityCoefficient,
                       double nozzleEfficiency,
                       boolean optimalNozzleDesign,
                       double nozzleExpansionRatio) {

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
     * See builder documentation {@link JSRMConfig.Builder}
     * @return See builder documentation {@link JSRMConfig.Builder}
     */
    public double getDensityRatio() {
        return densityRatio;
    }

    /**
     * See builder documentation {@link JSRMConfig.Builder}
     * @return See builder documentation {@link JSRMConfig.Builder}
     */
    public double getNozzleErosionInMillimeter() {
        return nozzleErosionInMillimeter;
    }

    /**
     * See builder documentation {@link JSRMConfig.Builder}
     * @return See builder documentation {@link JSRMConfig.Builder}
     */
    public double getCombustionEfficiencyRatio() {
        return combustionEfficiencyRatio;
    }

    /**
     * See builder documentation {@link JSRMConfig.Builder}
     * @return See builder documentation {@link JSRMConfig.Builder}
     */
    public double getAmbiantPressureInMPa() {
        return ambiantPressureInMPa;
    }

    /**
     * See builder documentation {@link JSRMConfig.Builder}
     * @return See builder documentation {@link JSRMConfig.Builder}
     */
    public double getErosiveBurningAreaRatioThreshold() {
        return erosiveBurningAreaRatioThreshold;
    }

    /**
     * See builder documentation {@link JSRMConfig.Builder}
     * @return See builder documentation {@link JSRMConfig.Builder}
     */
    public double getErosiveBurningVelocityCoefficient() {
        return erosiveBurningVelocityCoefficient;
    }

    /**
     * See builder documentation {@link JSRMConfig.Builder}
     * @return See builder documentation {@link JSRMConfig.Builder}
     */
    public double getNozzleEfficiency() {
        return nozzleEfficiency;
    }

    /**
     * See builder documentation {@link JSRMConfig.Builder}
     * @return See builder documentation {@link JSRMConfig.Builder}
     */
    public boolean isOptimalNozzleDesign() {
        return optimalNozzleDesign;
    }

    /**
     * See builder documentation {@link JSRMConfig.Builder}
     * @return See builder documentation {@link JSRMConfig.Builder}
     */
    public double getNozzleExpansionRatio() {
        return nozzleExpansionRatio;
    }

    public static class Builder {

        private double densityRatio = 0.95;
        private double nozzleErosionInMillimeter = 0.0;
        private double combustionEfficiencyRatio = 0.95;
        private double ambiantPressureInMPa = 0.101;
        private double erosiveBurningAreaRatioThreshold = 6.0;
        private double erosiveBurningVelocityCoefficient = 0;
        private double nozzleEfficiency = 0.85;
        private boolean optimalNozzleDesign = true;
        private double nozzleExpansionRatio;

        /**
         * Change density ratio (Grain actual density / Grain ideal density)
         * @param densityRatio typically 0.90 to 0.98 (default 0.95)
         * @return the builder
         */
        public Builder withDensityRatio(double densityRatio) {
            this.densityRatio = densityRatio;
            return this;
        }

        /**
         * Change increase in throat diameter due to erosion
         * @param nozzleErosionInMillimeter  in millimeter (default 0.0mm)
         * @return the builder
         */
        public Builder withNozzleErosionInMillimeter(double nozzleErosionInMillimeter) {
            this.nozzleErosionInMillimeter = nozzleErosionInMillimeter;
            return this;
        }

        /**
         * Change combustion efficiency ration
         * @param combustionEfficiencyRatio Use 0.95 for fine grain propellant. Use 0.93 for coarse grain propellant (default 0.95)
         * @return the builder
         */
        public Builder withCombustionEfficiencyRatio(double combustionEfficiencyRatio) {
            this.combustionEfficiencyRatio = combustionEfficiencyRatio;
            return this;
        }

        /**
         * Change local barometric pressure.
         * @param ambiantPressureInMPa typically 0.101 MPa at sea level (default 0.101Mpa)
         * @return the builder
         */
        public Builder withAmbiantPressureInMPa(double ambiantPressureInMPa) {
            this.ambiantPressureInMPa = ambiantPressureInMPa;
            return this;
        }

        /**
         * Change ratio of core to throat cross-sectional areas, above which no erosive burning occurs
         * @param erosiveBurningAreaRatioThreshold (default 6.0)
         * @return the builder
         */
        public Builder withErosiveBurningAreaRatioThreshold(double erosiveBurningAreaRatioThreshold) {
            this.erosiveBurningAreaRatioThreshold = erosiveBurningAreaRatioThreshold;
            return this;
        }

        /**
         * An empirical constant. Enter zero if no erosive burning expected.
         * @param erosiveBurningVelocityCoefficient Typically 0.0 to 1.0 (default 0.0)
         * @return the builder
         */
        public Builder withErosiveBurningVelocityCoefficient(double erosiveBurningVelocityCoefficient) {
            this.erosiveBurningVelocityCoefficient = erosiveBurningVelocityCoefficient;
            return this;
        }

        /**
         * Change nozzle efficiency
         * @param nozzleEfficiency For a well-contoured, smooth nozzle, typically 0.75 to 0.85 (default 0.85)
         * @return the builder
         */
        public Builder withNozzleEfficiency(double nozzleEfficiency) {
            this.nozzleEfficiency = nozzleEfficiency;
            return this;
        }

        /**
         * Use automatic calculation to obtain the optimum nozzle expansion ratio.
         * @param optimalNozzleDesign If set to false, you have to set nozzle expansion ratio with .withNozzleExpansionRatio()
         * @return the builder
         */
        public Builder withOptimalNozzleDesign(boolean optimalNozzleDesign) {
            this.optimalNozzleDesign = optimalNozzleDesign;
            return this;
        }

        /**
         * Change ratio of cross-sectional areas of nozzle exit  to throat.
         * Should be set if optimalNozzleDesign is false
         * @param nozzleExpansionRatio the expansion ratio
         * @return the builder
         */
        public Builder withNozzleExpansionRatio(double nozzleExpansionRatio) {
            this.nozzleExpansionRatio = nozzleExpansionRatio;
            this.optimalNozzleDesign = false;
            return this;
        }

        //TODO: javadoc
        public JSRMConfig createJSRMConfig() {
            //TODO : flag to use optimum nozzle ratio
            //TODO : envoyer une exception si optimalNozzleDesign = false et nozzleExpansionRatio=null
            //TODO : ou si optimalNozzleDesign = true et nozzleExpansionRatio!=null
            return new JSRMConfig(densityRatio, nozzleErosionInMillimeter, combustionEfficiencyRatio, ambiantPressureInMPa, erosiveBurningAreaRatioThreshold, erosiveBurningVelocityCoefficient, nozzleEfficiency, optimalNozzleDesign, nozzleExpansionRatio);
        }
    }
}
