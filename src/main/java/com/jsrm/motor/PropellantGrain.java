package com.jsrm.motor;

import com.jsrm.motor.propellant.Propellant;
import lombok.Data;
import net.objecthunter.exp4j.ExpressionBuilder;

@Data
public class PropellantGrain {

    private final Propellant propellant;
    private final double outerDiameter;
    private final double coreDiameter;
    private final double segmentLength;
    private final double numberOfSegment;
    private final GrainSurface outerSurface;
    private final GrainSurface endsSurface;
    private final GrainSurface coreSurface;

    public double getGrainVolume() {
        return new ExpressionBuilder("pi * (outerRadius^2 - coreRadius^2) * grainLenght")
                .variables("outerRadius", "coreRadius", "grainLenght")
                .build()
                .setVariable("outerRadius", outerDiameter/2)
                .setVariable("coreRadius", coreDiameter/2)
                .setVariable("grainLenght", getGrainLength())
                .evaluate();
    }

    public double getGrainLength() {
        return numberOfSegment* segmentLength;
    }

    public double getInitialWebThickness() {
        return (outerDiameter-coreDiameter)/2;
    }

}
