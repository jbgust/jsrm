package com.github.jbgust.jsrm.application.motor.propellant;

import com.github.jbgust.jsrm.application.exception.ChamberPressureOutOfBoundException;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.RangeMap;
import com.github.jbgust.jsrm.infra.propellant.BurnRateData;

import static com.google.common.collect.Range.*;

public enum PropellantType implements SolidPropellant {
    /**
     * KNDX = Potassium Nitrate/Dextrose, 65/35 O/F ratio
     */
    KNDX("KNDX = Potassium Nitrate/Dextrose, 65/35 O/F ratio",
            1.879, 1.1308, 1.1308, 42.42, 1710,
            new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    .put(lessThan(0.779135), new BurnRateData(8.87544496778536, 0.6193))
                    .put(closedOpen(0.779135, 2.571835), new BurnRateData(7.55278442387944, -0.0087))
                    .put(closedOpen(2.571835, 5.9297), new BurnRateData(3.84087990499602, 0.6882))
                    .put(closedOpen(5.9297, 8.501535), new BurnRateData(17.2041864098062, -0.1481))
                    //According to Richard Nakka burnrate Excel sheet the last range should be 8,501535 to 11,204375
                    // but the algorithm return also this BurnRateData even if the pressure exceed 11,204375 MPa
                    .put(atLeast(8.501535), new BurnRateData(4.77524086347659, 0.4417))
                    .build(), 1),

    /**
     * KNSB fine = potassium nitrate/sorbitol 65/35 O/F ratio, oxidizer finely milled
     */
    KNSB_FINE("KNSB fine = Potassium nitrate/sorbitol 65/35 O/F ratio, oxidizer finely milled",
            1.841, 1.1370, 1.1370, 39.90, 1600,
            new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    .put(lessThan(0.806715), new BurnRateData(10.7076837980331, 0.6247))
                    .put(closedOpen(0.806715, 1.50311), new BurnRateData(8.76328007101773, -0.3142))
                    .put(closedOpen(1.50311, 3.79225), new BurnRateData(7.85216579497841, -0.013))
                    .put(closedOpen(3.79225, 7.0329), new BurnRateData(3.90676830413905, 0.5354))
                    //According to Richard Nakka burnrate Excel sheet the last range should be 7.0329 to 10,67346
                    // but the algorithm return also this BurnRateData even if the pressure exceed 10,67346 MPa
                    .put(atLeast(7.0329), new BurnRateData(9.65320361987685, 0.0638))
                    .build(), 2),

    /**
     * KNSB coarse = potassium nitrate/sorbitol 65/35 O/F ratio, oxidizer granular or lightly milled prills
     */
    KNSB_COARSE("KNSB coarse = Potassium nitrate/sorbitol 65/35 O/F ratio, oxidizer granular or lightly milled prills",
            1.841, 1.1370, 1.1370, 39.90, 1600,
            new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    //According to Richard Nakka burnrate Excel sheet the last range should be 0.101 to 10,3
                    // but the algorithm return also this BurnRateData even if the pressure exceed 10,3 MPa
                    .put(all(), new BurnRateData(5.13, 0.22))
                    .build(), 3),

    /**
     * KNSU = potassium nitrate/sucrose 65/35 O/F ratio, oxidizer finely milled
     */
    KNSU("KNSU = Potassium nitrate/sucrose 65/35 O/F ratio, oxidizer finely milled",
            1.889, 1.133, 1.133, 42.02, 1720,
            new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    //According to Richard Nakka burnrate Excel sheet the last range should be 0.101 to 10,3
                    // but the algorithm return also this BurnRateData even if the pressure exceed 10,3 MPa
                    .put(all(), new BurnRateData(8.26, 0.319))
                    .build(), 4),

    /**
     * KNER coarse = potassium nitrate/erythritol 65/35 O/F ratio, oxidizer granular or lightly milled prills
     */
    KNER_COARSE("KNER coarse = Potassium nitrate/erythritol 65/35 O/F ratio, oxidizer granular or lightly milled prills",
            1.820, 1.14, 1.14, 38.58, 1608,
            new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    //According to Richard Nakka burnrate Excel sheet the last range should be 0.101 to 10,3
                    // but the algorithm return also this BurnRateData even if the pressure exceed 10,3 MPa
                    .put(all(), new BurnRateData(2.9, 0.4))
                    .build(), 5),

    /**
     * KNMN coarse = potassium nitrate/mannitol 65/35 O/F ratio, oxidizer granular or lightly milled prills
     */
    KNMN_COARSE("KNMN coarse = Potassium nitrate/mannitol 65/35 O/F ratio, oxidizer granular or lightly milled prills",
            1.854, 1.1363, 1.1363, 39.826, 1616,
            new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    .put(all(), new BurnRateData(5.13, 0.22))
                    .build(), 6),

    /**
     * KNXY = potassium nitrate/Xylitol 65/35 O/F ratio
     */
    KNXY("KNXY = Potassium nitrate/Xylitol 65/35 O/F ratio",
                        1.8654, 1.138, 1.138, 39.293, 1623,
                        new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    .put(all(), new BurnRateData(3.23, 0.483))
            .build(), 7),

    /**
     * KNFR = Potassium Nitrate/Fructose 65/35 O/F ratio
     */
    KNFR("KNFR = Potassium Nitrate/Fructose 65/35 O/F ratio",
                 1.942, 1.1308, 1.1308, 42.42, 1710,
                 new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    .put(all(), new BurnRateData(7.4, 0.25))
            .build(), 8),

    /**
     * KNPSB = Potassium Nitrate/Potassium Perchlorate/Sorbitol 35/30/35 O/O/F ratio
     */
    KNPSB("KNPSB = Potassium Nitrate/Potassium Perchlorate/Sorbitol 35/30/35 O/O/F ratio",
                 1.923, 1.163, 1.163, 36.39, 1858,
                 new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    .put(all(), new BurnRateData(6.5, 0.628))
            .build(), 9);

    private final RangeMap<Double, BurnRateData> byPressureData;
    private final String description;
    private final double idealMassDensity;
    private final double k2Ph;
    private final double k;
    private final double effectiveMolecularWeight;
    private final double chamberTemperature;
    private final int id;

    PropellantType(String description,
                   double idealMassDensity, double k2Ph, double k, double effectiveMolecularWeight,
                   double chamberTemperature, RangeMap<Double, BurnRateData> byPressureData, int id) {
        this.byPressureData = byPressureData;
        this.description = description;
        this.idealMassDensity = idealMassDensity;
        this.k2Ph = k2Ph;
        this.k = k;
        this.effectiveMolecularWeight = effectiveMolecularWeight;
        this.chamberTemperature = chamberTemperature;
        this.id = id;
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
            return burnRateData.burnRateCoefficient();
        } else {
            throw new ChamberPressureOutOfBoundException(
                    name() + " has no burn rate coefficient for this pressure ("+chamberPressure+") should be in range " + byPressureData.span());
        }
    }

    @Override
    public double getPressureExponent(double chamberPressure) throws ChamberPressureOutOfBoundException {
        BurnRateData burnRateData = byPressureData.get(chamberPressure);
        if (burnRateData !=null){
            return burnRateData.pressureExponent();
        } else {
            throw new ChamberPressureOutOfBoundException(
                    name() + " has no pressure exponent for this pressure ("+chamberPressure+") should be in range " + byPressureData.span());
        }
    }

    public int getId() {
        return id;
    }
}
