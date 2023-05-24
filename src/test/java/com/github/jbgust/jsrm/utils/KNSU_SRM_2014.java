package com.github.jbgust.jsrm.utils;

import com.github.jbgust.jsrm.application.exception.ChamberPressureOutOfBoundException;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.infra.propellant.BurnRateData;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.RangeMap;

import static com.google.common.collect.Range.*;

/**
 * LEGACY propellant config from SRM 2014 used for qualifying JSRM
 */
public final class KNSU_SRM_2014 implements SolidPropellant {

    RangeMap<Double, BurnRateData> byPressureData = new ImmutableRangeMap.Builder<Double, BurnRateData>()
            //According to Richard Nakka burnrate Excel sheet the last range should be 0.101 to 10,3
            // but the algorithm return also this BurnRateData even if the pressure exceed 10,3 MPa
            .put(all(), new BurnRateData(8.26, 0.319))
            .build();

    @Override
    public String getDescription() {
        return "KNSU_SRM_2014";
    }

    @Override
    public double getIdealMassDensity() {
        return 1.889;
    }

    @Override
    public double getK2Ph() {
        return 1.044;
    }

    @Override
    public double getK() {
        return 1.133;
    }

    @Override
    public double getEffectiveMolecularWeight() {
        return 41.98;
    }

    @Override
    public double getChamberTemperature() {
        return 1720;
    }

    @Override
    public double getBurnRateCoefficient(double chamberPressure) throws ChamberPressureOutOfBoundException {
        return byPressureData.get(chamberPressure).getBurnRateCoefficient();
    }

    @Override
    public double getPressureExponent(double chamberPressure) throws ChamberPressureOutOfBoundException {
        return byPressureData.get(chamberPressure).getPressureExponent();
    }
}
