package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.infra.function.HollowCircleAreaFunction;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static java.lang.Math.PI;

public class RodAndTubeGrain implements GrainConfigutation {

    /**
     * Rod diameter
     */
    private final double rodDiameter;

    /**
     *
     */
    private final double tubeOuterDiameter;

    /**
     * Tube outer diameter
     */
    private final double tubeInnerDiameter;

    /**
     * Number of segment
     */
    private final int numberOfSegment;

    /**
     * Grain length
     */
    private final double length;


    private final GrainSurface endSurface;

    public RodAndTubeGrain(double rodDiameter, double tubeOuterDiameter, double tubeInnerDiameter, int numberOfSegment, double length, GrainSurface endSurface) {
        this.rodDiameter = rodDiameter;
        this.tubeOuterDiameter = tubeOuterDiameter;
        this.tubeInnerDiameter = tubeInnerDiameter;
        this.numberOfSegment = numberOfSegment;
        this.length = length;
        this.endSurface = endSurface;
    }

    @Override
    public double getGrainEndSurface(double burnProgression) {
        return rodArea(burnProgression) + tubeArea(burnProgression);
    }

    @Override
    public double getGrainVolume(double burnProgression) {
        double grainLength = getGrainLength(burnProgression) * numberOfSegment;
        return getGrainEndSurface(burnProgression) * grainLength;
    }

    @Override
    public double getBurningArea(double burnProgression) {
        double rodPerimeter = computeRodRadius(burnProgression) * 2 * PI;

        double tubeInnerPerimeter = 0;
        double computeTubeInnerDiameter = computeTubeInnerDiameter(burnProgression);
        if (computeTubeInnerDiameter <= tubeOuterDiameter) {
            tubeInnerPerimeter = computeTubeInnerDiameter * PI;
        }
        double endBurningSurfaces = 0;
        if (endSurface == EXPOSED) {
            endBurningSurfaces = getGrainEndSurface(burnProgression) * 2 * numberOfSegment;
        }
        return (rodPerimeter + tubeInnerPerimeter) * getGrainLength(burnProgression) * numberOfSegment + endBurningSurfaces;
    }

    @Override
    public double getXincp(int numberOfPoints) {
        return webThickness() / numberOfPoints;
    }

    /**
     * Return the largest web thickness between rod and tube
     *
     * @return the web thickness to find xincp
     */
    public double webThickness() {
        double rodThickness = rodDiameter / 2;
        double tubeThickness = (tubeOuterDiameter - tubeInnerDiameter) / 2;

        return Math.max(rodThickness, tubeThickness);
    }

    /**
     * Compute the rod area during burn.
     *
     * @param burnProgression
     * @return
     */
    private double rodArea(double burnProgression) {
        return PI * Math.pow(computeRodRadius(burnProgression), 2);
    }

    private double computeRodRadius(double burnProgression) {
        double newRodRadius = rodDiameter / 2 - computeRegression(burnProgression);
        if (newRodRadius < 0d) {
            return 0d;
        } else {
            return newRodRadius;
        }
    }

    private double computeRegression(double burnProgression) {
        return webThickness() * burnProgression;
    }

    /**
     * Compute the tube web regression.
     *
     * @param burnProgression
     * @return
     */
    private double tubeArea(double burnProgression) {
        double newTubeInnerDiameter = computeTubeInnerDiameter(burnProgression);

        if (newTubeInnerDiameter > tubeOuterDiameter) {
            return 0d;
        } else {
            HollowCircleAreaFunction hollowCircleAreaFunction = new HollowCircleAreaFunction();
            return hollowCircleAreaFunction.runFunction(
                    tubeOuterDiameter,
                    newTubeInnerDiameter);
        }
    }

    private double computeTubeInnerDiameter(double burnProgression) {
        double newTubeInnerDiameter = tubeInnerDiameter + 2 * computeRegression(burnProgression);
        return newTubeInnerDiameter;
    }

    private double getGrainLength(double burnProgression) {
        if (endSurface == EXPOSED) {
            return length - 2 * computeRegression(burnProgression);
        } else {
            return length;
        }
    }

    @Override
    public void checkConfiguration(SolidRocketMotor solidRocketMotor) throws InvalidMotorDesignException {
        if (rodDiameter <= 0) {
            throw new InvalidMotorDesignException("Rod diameter should be > 0");
        }

        if (rodDiameter >= tubeInnerDiameter) {
            throw new InvalidMotorDesignException("Rod diameter should be < than tube inner diameter");
        }

        if (tubeOuterDiameter <= 0) {
            throw new InvalidMotorDesignException("Tube outer diameter should be > 0");
        }

        if (tubeOuterDiameter <= tubeInnerDiameter) {
            throw new InvalidMotorDesignException("Tube outer diameter should be > than tube inner diameter");
        }

        if (tubeOuterDiameter > solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter()) {
            throw new InvalidMotorDesignException("Combution chamber diameter should be >= than tube outer diameter");
        }

        if (numberOfSegment <= 0) {
            throw new InvalidMotorDesignException("Number of segment should be > 0");
        }

        double webThickness = webThickness();
        if (length <= webThickness) {
            throw new InvalidMotorDesignException("Grain length should be > " + webThickness);
        }

        if (length * numberOfSegment > solidRocketMotor.getCombustionChamber().getChamberLengthInMillimeter()) {
            throw new InvalidMotorDesignException("Combustion chamber length should be >= than Grain total length");
        }
    }
}
