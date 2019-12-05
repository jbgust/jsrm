package com.github.jbgust.jsrm.utils;

import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;
import com.github.jbgust.jsrm.application.motor.grain.HollowCylinderGrain;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;

public class PropellantGrainBuilder {

    private double coreDiameter = 10;
    private double segmentLength = 50;
    private double numberOfSegments = 1;
    private GrainSurface outerSurface = INHIBITED;
    private GrainSurface endsSurface = INHIBITED;
    private GrainSurface coreSurface = EXPOSED;
    private SolidPropellant propellantType = KNDX;
    private double outerDiameter = 20;

    public PropellantGrain build() {
        return new PropellantGrain(propellantType,
                new HollowCylinderGrain(outerDiameter, coreDiameter, segmentLength, numberOfSegments, outerSurface, endsSurface, coreSurface));
    }

    public PropellantGrainBuilder withCoreSurface(GrainSurface surface){
        coreSurface = surface;
        return this;
    }

    public PropellantGrainBuilder withOuterSurface(GrainSurface surface) {
        outerSurface = surface;
        return this;
    }

    public PropellantGrainBuilder withEndsSurface(GrainSurface surface) {
        endsSurface = surface;
        return this;
    }

    public PropellantGrainBuilder withNumberOfSegments(double numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
        return this;
    }

    public PropellantGrainBuilder withCoreDiameter(double coreDiameter) {
        this.coreDiameter = coreDiameter;
        return this;
    }

    public PropellantGrainBuilder withOuterDiameter(double outerDiameter) {
        this.outerDiameter = outerDiameter;
        return this;
    }

    public PropellantGrainBuilder withSegmentLength(double segmentLength) {
        this.segmentLength = segmentLength;
        return this;
    }

}
