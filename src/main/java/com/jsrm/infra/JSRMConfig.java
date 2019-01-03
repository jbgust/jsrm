package com.jsrm.infra;

import lombok.Builder;

@Builder
public class JSRMConfig {

    /**
     * Density ratio (Grain actual density / Grain ideal density), typically 0.90 to 0.98 (default 0.95)
     */
    @Builder.Default
    private final double densityRatio = 0.95;

    /**
     * Increase in throat diameter due to erosion in millimeter (default 0.0mm)
     */
    @Builder.Default
    private final double nozzleErosionInMillimeter = 0.0;

    /**
     * Use 0.95 for fine grain propellant. Use 0.93 for coarse grain propellant (default 0.95)
     */
    @Builder.Default
    private final double combustionEfficiencyRatio = 0.95;

    /**
     * Local barometric pressure, typically 0.101 MPa.  at sea level default(0.101Mpa)
     */
    @Builder.Default
    private final double ambiantPressureInMPa = 0.101;


    /**
     * Ratio of core to throat cross-sectional areas, above which no erosive burning occurs (default 6.0)
     */
    @Builder.Default
    private final double erosiveBurningAreaRatioThreshold = 6.0;


    /**
     * An empirical constant. Enter zero if no erosive burning expected. Typically 0.0 to 1.0 (default 0.0)
     */
    @Builder.Default
    private final double erosiveBurningVelocityCoefficient = 0;

    /**
     * For a well-contoured, smooth nozzle, typically 0.75 to 0.85 (default 0.85)
     */
    @Builder.Default
    private final double nozzleEfficiency = 0.85;

    //TODO : flag to use optimum nozzle ratio
    /**
     * Use automatic calculation to obtain the optimum nozzle expansion ratio.
     * If set to false, you have to set nozzle expansion ratio with JSRMConfig.builder().nozzleExpansionRatio()
     */
    @Builder.Default
    private final boolean optimalNozzleDesign = true;

    //TODO : envoyer une exception si optimalNozzleDesign = false et nozzleExpansionRatio=null
    //TODO : ou si optimalNozzleDesign = true et nozzleExpansionRatio!=null
    /**
     * Ratio of cross-sectional areas of nozzle exit  to throat.
     * Should be set if optimalNozzleDesign is false
     */
    private final double nozzleExpansionRatio;
}
