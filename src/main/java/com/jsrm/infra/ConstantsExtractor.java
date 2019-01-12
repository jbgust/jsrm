package com.jsrm.infra;

import com.jsrm.application.JSRMConfig;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.application.motor.propellant.PropellantGrain;
import com.jsrm.application.motor.propellant.SolidPropellant;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.infra.JSRMConstant.*;
import static java.util.stream.Collectors.toMap;

public class ConstantsExtractor {

    public static Map<JSRMConstant, Double> extract(SolidRocketMotor solidRocketMotor, JSRMConfig config, int propellantId) {
        PropellantGrain propellantGrain = solidRocketMotor.getPropellantGrain();

        double twoValue = (propellantGrain.getOuterDiameter() - propellantGrain.getCoreDiameter()) / 2;

        HashMap<JSRMConstant, Double> constants = new HashMap<>();
        constants.put(ci, (double) propellantGrain.getCoreSurface().value());
        constants.put(osi, (double) propellantGrain.getOuterSurface().value());
        constants.put(ei, (double) propellantGrain.getEndsSurface().value());
        constants.put(xincp, twoValue / (NUMBER_LINE_DURING_BURN_CALCULATION - 1));
        constants.put(dc, solidRocketMotor.getMotorChamber().getChamberInnerDiameter());
        constants.put(n, propellantGrain.getNumberOfSegment());
        constants.put(vc, solidRocketMotor.getMotorChamber().getVolume());
        constants.put(dto, solidRocketMotor.getThroatDiameter());
        constants.put(erate, config.getNozzleErosionInMillimeter());
        constants.put(two, twoValue);
        constants.put(gstar, config.getErosiveBurningAreaRatioThreshold());


        SolidPropellant propellantType = propellantGrain.getPropellant();
        constants.put(kv, config.getErosiveBurningVelocityCoefficient());
        constants.put(pbd, PBD);
        constants.put(rat, UNIVERSAL_GAS_CONSTANT / propellantType.getEffectiveMolecularWeight());
        constants.put(to, config.getCombustionEfficiencyRatio() * propellantType.getChamberTemperature());
        constants.put(patm, config.getAmbiantPressureInMPa());
        constants.put(k, propellantType.getK());
        constants.put(JSRMConstant.propellantId, new Double(propellantId));
        constants.put(rhopgrain, propellantType.getIdealMassDensity() * config.getDensityRatio());

        return constants;
    }

    public static Map<String, Double> toCalculationFormat(Map<JSRMConstant, Double> jsrmConstantDoubleMap) {
        return jsrmConstantDoubleMap.entrySet().stream()
                .collect(toMap(entry -> entry.getKey().name(), Map.Entry::getValue));
    }
}