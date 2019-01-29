package com.github.jbgust.jsrm.utils;

import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.GrainSurface;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;

import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.*;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;

public class SolidRocketMotorBuilder {

    private double grainOuterDiameter = 69d;
    private double grainCoreDiameter = 20d;
    private double grainSegmentLength = 115d;
    private double numberOfSegment = 4d;
    private double chamberInnerDiameter = 75d;
    private double chamberLength = 470d;
    private double throatDiameter = 17.3985248919802;
    private SolidPropellant propellant = KNDX;
    private GrainSurface outerSurface = INHIBITED;
    private GrainSurface endsSurface = EXPOSED;
    private GrainSurface coreSurface = EXPOSED;

    /**
     * Create same solid rocket motor that is used in SRM_2014.xls @see <a href="https://www.nakka-rocketry.net/softw.html">SRM_2014.xls</a>
     * @return the solid rocket motor described in SRM_2014.xls
     */
    public static SolidRocketMotor createMotorAsSRM_2014ExcelFile() {
        return new SolidRocketMotorBuilder().build();
    }

    public SolidRocketMotor build() {
        PropellantGrain propellantGrain = new PropellantGrain(propellant, grainOuterDiameter, grainCoreDiameter,
                grainSegmentLength, numberOfSegment,
                outerSurface, endsSurface, coreSurface);

        CombustionChamber combustionChamber = new CombustionChamber(chamberInnerDiameter, chamberLength);

        return new SolidRocketMotor(propellantGrain, combustionChamber, throatDiameter);

    }
    public SolidRocketMotorBuilder withGrainOuterDiameter(double grainOuterDiameter) {
        this.grainOuterDiameter = grainOuterDiameter;
        return this;
    }
    public SolidRocketMotorBuilder withGrainCoreDiameter(double grainCoreDiameter) {
        this.grainCoreDiameter = grainCoreDiameter;
        return this;
    }
    public SolidRocketMotorBuilder withGrainSegmentLength(double grainSegmentLength) {
        this.grainSegmentLength = grainSegmentLength;
        return this;
    }
    public SolidRocketMotorBuilder withNumberOfSegment(double numberOfSegment) {
        this.numberOfSegment = numberOfSegment;
        return this;
    }
    public SolidRocketMotorBuilder withChamberInnerDiameter(double chamberInnerDiameter) {
        this.chamberInnerDiameter = chamberInnerDiameter;
        return this;
    }
    public SolidRocketMotorBuilder withChamberLength(double chamberLength) {
        this.chamberLength = chamberLength;
        return this;
    }
    public SolidRocketMotorBuilder withThroatDiameter(double throatDiameter) {
        this.throatDiameter = throatDiameter;
        return this;
    }
    public SolidRocketMotorBuilder withPropellant(SolidPropellant propellant) {
        this.propellant = propellant;
        return this;
    }
    public SolidRocketMotorBuilder withOuterSurface(GrainSurface outerSurface) {
        this.outerSurface = outerSurface;
        return this;
    }
    public SolidRocketMotorBuilder withEndsSurface(GrainSurface endsSurface) {
        this.endsSurface = endsSurface;
        return this;
    }

    public SolidRocketMotorBuilder withCoreSurface(GrainSurface coreSurface) {
        this.coreSurface = coreSurface;
        return this;
    }
}