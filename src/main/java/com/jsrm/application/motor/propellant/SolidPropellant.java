package com.jsrm.application.motor.propellant;

import com.jsrm.application.exception.ChamberPressureOutOfBoundException;

public interface SolidPropellant {

    /**
     * @return a simple description of the propellant ex: "KNDX = Potassium Nitrate/Dextrose, 65/35 O/F ratio"
     */
    String getDescription();

    /**
     * @return Grain mass density, ideal [g/cm3]
     */
    double getIdealMassDensity();

    /**
     * @return Ratio of specific heats, 2-ph.
     */
    double getK2Ph();

    /**
     * @return Ratio of specific heats, mixture
     */
    double getK();

    /**
     *
     * @return Effective molecular wt. [kg/kmol]
     */
    double getEffectiveMolecularWeight();

    /**
     * @return Chamber temperature [°K]
     */
    double getChamberTemperature();

    /**
     * @param chamberPressure in [Mpa]
     * @return Burn rate coefficient by chamber pressure  [mm/s]
     * @throws ChamberPressureOutOfBoundException if a pressure data is not find
     */
    double getBurnRateCoefficient(double chamberPressure) throws ChamberPressureOutOfBoundException;

    /**
     * @param chamberPressure in [Mpa]
     * @return Pressure exponent valid at Po
     * @throws ChamberPressureOutOfBoundException if a pressure data is not find
     */
    double getPressureExponent(double chamberPressure) throws ChamberPressureOutOfBoundException;

}
