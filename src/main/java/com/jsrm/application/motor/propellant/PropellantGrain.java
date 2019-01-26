package com.jsrm.application.motor.propellant;

import net.objecthunter.exp4j.ExpressionBuilder;

public class PropellantGrain {

    private final SolidPropellant propellant;
    private final double outerDiameter;
    private final double coreDiameter;
    private final double segmentLength;
    private final double numberOfSegment;
    private final GrainSurface outerSurface;
    private final GrainSurface endsSurface;
    private final GrainSurface coreSurface;

    /**
     * Create an Hollow cylindrical propellant grain
     * @param propellant propellant used (for default propellant see {@link PropellantType})
     * @param outerDiameter outer grain diameter in millimeter
     * @param coreDiameter core  grain diameter in millimeter
     * @param segmentLength the length of a segment gran in millimeter. Example : if using 4 grains of 34mm each, segmentLength is 34.
     * @param numberOfSegment the number of segment
     * @param outerSurface outer surface is exposed to combustion or inhibited
     * @param endsSurface end segments surface are exposed to combustion or inhibited
     * @param coreSurface core surface is exposed to combustion or inhibited
     */
    public PropellantGrain(SolidPropellant propellant, double outerDiameter, double coreDiameter, double segmentLength, double numberOfSegment, GrainSurface outerSurface, GrainSurface endsSurface, GrainSurface coreSurface) {
        this.propellant = propellant;
        this.outerDiameter = outerDiameter;
        this.coreDiameter = coreDiameter;
        this.segmentLength = segmentLength;
        this.numberOfSegment = numberOfSegment;
        this.outerSurface = outerSurface;
        this.endsSurface = endsSurface;
        this.coreSurface = coreSurface;
    }

    /**
     * The total length of the grain (segmentLength * numberOfSegment)
     * @return the grain length in millimeter
     */
    public double getGrainVolume() {
        return new ExpressionBuilder("pi * (outerRadius^2 - coreRadius^2) * grainLength")
                .variables("outerRadius", "coreRadius", "grainLength")
                .build()
                .setVariable("outerRadius", outerDiameter/2)
                .setVariable("coreRadius", coreDiameter/2)
                .setVariable("grainLength", getGrainLength())
                .evaluate();
    }

    public double getGrainLength() {
        return numberOfSegment* segmentLength;
    }

    public double getInitialWebThickness() {
        return (outerDiameter-coreDiameter)/2;
    }

    public SolidPropellant getPropellant() {
        return propellant;
    }

    public double getOuterDiameter() {
        return outerDiameter;
    }

    public double getCoreDiameter() {
        return coreDiameter;
    }

    public double getSegmentLength() {
        return segmentLength;
    }

    public double getNumberOfSegment() {
        return numberOfSegment;
    }

    public GrainSurface getOuterSurface() {
        return outerSurface;
    }

    public GrainSurface getEndsSurface() {
        return endsSurface;
    }

    public GrainSurface getCoreSurface() {
        return coreSurface;
    }
}
