package com.github.jbgust.jsrm.application.motor.propellant;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;

public interface GrainConfigutation {

    /**
     * Compute the end grain surface during burn
     * @param burnProgression (0 to 1, 0 = before burn start ; 1 = grain completly burn)
     * @return the end grain surface in mm^2
     */
    double getGrainEndSurface(double burnProgression);

    /**
     * Compute the grain volume during burn
     * @param burnProgression (0 to 1, 0 = before burn start ; 1 = grain completly burn)
     * @return the grain volume in mm^3
     */
    double getGrainVolume(double burnProgression);

    /**
     * Compute the burning area during burn
     * @param burnProgression (0 to 1, 0 = before burn start ; 1 = grain completly burn)
     * @return the burning area
     */
    double getBurningArea(double burnProgression);

    /**
     * This method should return the length that will be remove in each computation iteration
     * ex : for hollow cylinder grain this will be : initial web thickness / numberOfPoints
     * @param numberOfPoints the number of iteration during the computation
     * @return xincp (cf. SRM_2014.xls)
     */
    double getXincp(int numberOfPoints);

    void checkConfiguration(SolidRocketMotor solidRocketMotor) throws InvalidMotorDesignException;

}
