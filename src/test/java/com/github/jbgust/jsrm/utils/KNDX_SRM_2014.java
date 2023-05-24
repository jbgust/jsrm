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
public final class KNDX_SRM_2014 implements SolidPropellant {

    RangeMap<Double, BurnRateData> byPressureData = new ImmutableRangeMap.Builder<Double, BurnRateData>()
             .put(lessThan(0.779135), new BurnRateData(8.87544496778536, 0.6193))
            .put(closedOpen(0.779135, 2.571835), new BurnRateData(7.55278442387944, -0.0087))
            .put(closedOpen(2.571835, 5.9297), new BurnRateData(3.84087990499602, 0.6882))
            .put(closedOpen(5.9297, 8.501535), new BurnRateData(17.2041864098062, -0.1481))
            //According to Richard Nakka burnrate Excel sheet the last range should be 8,501535 to 11,204375
            // but the algorithm return also this BurnRateData even if the pressure exceed 11,204375 MPa
            .put(atLeast(8.501535), new BurnRateData(4.77524086347659, 0.4417))
            .build();
    @Override
    public String getDescription() {
        return "KNDX_SRM_2014";
    }

    @Override
    public double getIdealMassDensity() {
        return 1.879;
    }

    @Override
    public double getK2Ph() {
        return 1.043;
    }

    @Override
    public double getK() {
        return 1.1308;
    }

    @Override
    public double getEffectiveMolecularWeight() {
        return 42.39;
    }

    @Override
    public double getChamberTemperature() {
        return 1710;
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
