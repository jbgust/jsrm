package com.jsrm.motor.propellant;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.RangeMap;

import static com.google.common.collect.Range.atLeast;
import static com.google.common.collect.Range.closedOpen;

public enum PropellantType implements SolidPropellant {
    KNDX(
            "KNDX = Potassium Nitrate/Dextrose, 65/35 O/F ratio",
            1.879, 1.043, 1.131, 42.39, 1710,
            new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    .put(closedOpen(0.1, 0.779135), new BurnRateData(8.87544496778536, 0.6193))
                    .put(closedOpen(0.779135, 2.571835), new BurnRateData(7.55278442387944, -0.0087))
                    .put(closedOpen(2.571835, 5.9297), new BurnRateData(3.84087990499602, 0.6882))
                    .put(closedOpen(5.9297, 8.501535), new BurnRateData(17.2041864098062, -0.1481))
                    //According to Richard Nakka burnrate Excel sheet the last range should be 8,501535 to 11,204375
                    // but the algorithm return also this BurnRateData if the pressure exceed 11,204375 MPa
                    .put(atLeast(8.501535), new BurnRateData(4.77524086347659, 0.4417))
                    .build());

    private final RangeMap<Double, BurnRateData> byPressureData;
    private final String description;
    private final double idealMassDensity;
    private final double k2Ph;
    private final double k;
    private final double effectiveMolecularWeight;
    private final double chamberTemperature;

    PropellantType(String description,
                   double idealMassDensity, double k2Ph, double k, double effectiveMolecularWeight,
                   double chamberTemperature, RangeMap<Double, BurnRateData> byPressureData) {
        this.byPressureData = byPressureData;
        this.description = description;
        this.idealMassDensity = idealMassDensity;
        this.k2Ph = k2Ph;
        this.k = k;
        this.effectiveMolecularWeight = effectiveMolecularWeight;
        this.chamberTemperature = chamberTemperature;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getIdealMassDensity() {
        return idealMassDensity;
    }

    @Override
    public double getK2Ph() {
        return k2Ph;
    }

    @Override
    public double getK() {
        return k;
    }

    @Override
    public double getEffectiveMolecularWeight() {
        return effectiveMolecularWeight;
    }

    @Override
    public double getChamberTemperature() {
        return chamberTemperature;
    }

    @Override
    public double getBurnRateCoefficient(double chamberPressure) throws ChamberPressureOutOfBoundException {
        BurnRateData burnRateData = byPressureData.get(chamberPressure);
        if (burnRateData !=null){
            return burnRateData.getBurnRateCoefficient();
        } else {
            throw new ChamberPressureOutOfBoundException(
                    name() + " has no burn rate coefficient for this pressure ("+chamberPressure+") should be in range " + byPressureData.span());
        }
    }

    @Override
    public double getPressureExponent(double chamberPressure) throws ChamberPressureOutOfBoundException {
        BurnRateData burnRateData = byPressureData.get(chamberPressure);
        if (burnRateData !=null){
            return burnRateData.getPressureExponent();
        } else {
            throw new ChamberPressureOutOfBoundException(
                    name() + " has no pressure exponent for this pressure ("+chamberPressure+") should be in range " + byPressureData.span());
        }
    }

}
