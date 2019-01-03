package com.jsrm.infra;

import com.jsrm.core.JSRMConstant;
import com.jsrm.motor.SolidRocketMotor;
import com.jsrm.motor.propellant.SolidPropellant;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.core.JSRMConstant.*;
import static java.util.stream.Collectors.toMap;

public class Extract {

    private static double numberOfInterval = 834d;

    public static Map<JSRMConstant, Double> extractConstants(SolidRocketMotor solidRocketMotor) {
        double twoValue = 24.5;
        HashMap<JSRMConstant, Double> constants = new HashMap<>();
        constants.put(ci, (double) solidRocketMotor.getPropellantGrain().getCoreSurface().value());
        constants.put(osi, (double) solidRocketMotor.getPropellantGrain().getOuterSurface().value());
        constants.put(ei, (double) solidRocketMotor.getPropellantGrain().getEndsSurface().value());
        constants.put(xincp, twoValue / numberOfInterval);
        constants.put(dc, solidRocketMotor.getMotorChamber().getChamberInnerDiameter());
        constants.put(n, solidRocketMotor.getPropellantGrain().getNumberOfSegment());
        constants.put(vc, solidRocketMotor.getMotorChamber().getVolume());
        constants.put(dto, solidRocketMotor.getThroatDiameter());
        constants.put(erate, solidRocketMotor.getNozzleErosion());
        constants.put(two, twoValue);
        constants.put(gstar, solidRocketMotor.getErosiveBurningArea());

        //TODO
        SolidPropellant propellantType = solidRocketMotor.getPropellantGrain().getPropellantType();
        constants.put(kv, 0d);
        constants.put(pbd, 0d);
        constants.put(rat, 8314/ propellantType.getEffectiveMolecularWeight());
         constants.put(to, .95*propellantType.getChamberTemperature());
         constants.put(patm, 0.101);
         constants.put(k, propellantType.getK());
         constants.put(propellantId, 1.0);//TODO: retrieve propellant ID
         constants.put(rhopgrain, propellantType.getIdealMassDensity()*.95);
        return constants;
    }

    public static Map<String, Double> toCalculationFormat(Map<JSRMConstant, Double> jsrmConstantDoubleMap) {
        return jsrmConstantDoubleMap.entrySet().stream()
                .collect(toMap(entry -> entry.getKey().name(), Map.Entry::getValue));
    }
}