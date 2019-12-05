package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.infra.function.HollowCircleAreaFunction;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;

public class HollowCylinderGrain implements GrainConfigutation {

    private final double outerDiameter;
    private final double coreDiameter;
    private final double segmentLength;
    private final double numberOfSegment;

    private final GrainSurface outerSurface;
    private final GrainSurface endsSurface;
    private final GrainSurface coreSurface;

    /**
     * Create an Hollow cylindrical propellant grain
     * @param outerDiameter outer grain diameter in millimeter
     * @param coreDiameter core  grain diameter in millimeter
     * @param segmentLength the length of a segment gran in millimeter. Example : if using 4 grains of 34mm each, segmentLength is 34.
     * @param numberOfSegment the number of segment
     * @param outerSurface outer surface is exposed to combustion or inhibited
     * @param endsSurface end segments surface are exposed to combustion or inhibited
     * @param coreSurface core surface is exposed to combustion or inhibited
     */
    public HollowCylinderGrain(double outerDiameter, double coreDiameter, double segmentLength, double numberOfSegment, GrainSurface outerSurface, GrainSurface endsSurface, GrainSurface coreSurface) {
        this.outerDiameter = outerDiameter;
        this.coreDiameter = coreDiameter;
        this.segmentLength = segmentLength;
        this.numberOfSegment = numberOfSegment;
        this.outerSurface = outerSurface;
        this.endsSurface = endsSurface;
        this.coreSurface = coreSurface;
    }

    @Override
    public double getGrainEndSurface(double burnProgression) {
        HollowCircleAreaFunction hollowCircleAreaFunction = new HollowCircleAreaFunction();
        return hollowCircleAreaFunction.runFunction(
                getOuterDiameter(burnProgression),
                getCoreDiameter(burnProgression));
    }

    @Override
    public double getGrainVolume(double burnProgression) {
        return getGrainEndSurface(burnProgression) * getGrainLength(burnProgression);
    }

    @Override
    public double getBurningArea(double burnProgression) {
        return getGrainEndBurningSurface(burnProgression) + getGrainCoreBurningSurface(burnProgression) + getGrainOuterBurningSurface(burnProgression);
    }

    @Override
    public double getXincp(int numberOfPoints) {
        return (outerDiameter - coreDiameter) / (numberOfPoints-1) / 2 / getWebRegressionDivider();
    }

    @Override
    public void checkConfiguration(SolidRocketMotor solidRocketMotor) throws InvalidMotorDesignException {
        CombustionChamber combustionChamber = solidRocketMotor.getCombustionChamber();

        if(coreDiameter < solidRocketMotor.getThroatDiameterInMillimeter()){
            throw new InvalidMotorDesignException("Throat diameter should be <= than grain core diameter");
        }

        if(outerDiameter > combustionChamber.getChamberInnerDiameterInMillimeter()) {
            throw new InvalidMotorDesignException("Combution chamber diameter should be >= than grain outer diameter");
        }

        if(outerDiameter <= coreDiameter) {
            throw new InvalidMotorDesignException("Grain outer diameter should be > than grain core diameter");
        }

        double totalGrainLength = segmentLength * numberOfSegment;
        if(totalGrainLength > combustionChamber.getChamberLengthInMillimeter()) {
            throw new InvalidMotorDesignException("Combustion chamber length should be >= than Grain total length");
        }

        if(INHIBITED == coreSurface && INHIBITED == outerSurface) {
            throw new InvalidMotorDesignException("The motor should have at least core surface or outer surface exposed.");
        }
    }

    private double getGrainEndBurningSurface(double burnProgression) {
        return endsSurface.value() * getGrainEndSurface(burnProgression) * numberOfSegment * 2;
    }

    private double getGrainOuterBurningSurface(double burnProgression) {
        return outerSurface.value() * Math.PI * getOuterDiameter(burnProgression) * getGrainLength(burnProgression);
    }

    private double getGrainCoreBurningSurface(double burnProgression) {
        return coreSurface.value() * Math.PI * getCoreDiameter(burnProgression) * getGrainLength(burnProgression);
    }

    private double getGrainLength(double burnProgression) {
        return (segmentLength - webRegression(burnProgression) * endsSurface.value()) * numberOfSegment;
    }

    private double getOuterDiameter(double burnProgression) {
        return outerDiameter - webRegression(burnProgression) * outerSurface.value();
    }

    private double getCoreDiameter(double burnProgression) {
        return coreDiameter + webRegression(burnProgression) * coreSurface.value();
    }

    /**
     * Compute the web regression. If core and outer are exposed the webregression is divided by 2 (see getWebRegressionDivider()), because burning is laod
     * balanced to both side and the end surfaces should burn as the same speed.
     * @param burnProgression
     * @return
     */
    private double webRegression(double burnProgression) {
        return (outerDiameter - coreDiameter) * burnProgression / getWebRegressionDivider();
    }

    /**
     * If both core and outer are exposed, this return 2 see webRegression() for more information
     * @return
     */
    private int getWebRegressionDivider() {
        return coreSurface.value() + outerSurface.value();
    }
}
