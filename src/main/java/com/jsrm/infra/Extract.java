package com.jsrm.infra;

import com.jsrm.motor.SolidRocketMotor;
import com.jsrm.motor.propellant.PropellantType;

import java.util.HashMap;
import java.util.Map;

import static com.jsrm.core.JSRMConstant.*;

public class Extract {

    private static double numberOfInterval = 834d;

    public static Map<String, Double> extractConstants(SolidRocketMotor solidRocketMotor) {
        double twoValue = 24.5;
        HashMap<String, Double> constants = new HashMap<>();
        constants.put(ci.name(), (double) solidRocketMotor.getPropellantGrain().getCoreSurface().value());
        constants.put(osi.name(), (double) solidRocketMotor.getPropellantGrain().getOuterSurface().value());
        constants.put(ei.name(), (double) solidRocketMotor.getPropellantGrain().getEndsSurface().value());
        constants.put(xincp.name(), twoValue / numberOfInterval);
        constants.put(dc.name(), solidRocketMotor.getMotorChamber().getChamberInnerDiameter());
        constants.put(n.name(), solidRocketMotor.getPropellantGrain().getNumberOfSegment());
        constants.put(vc.name(), solidRocketMotor.getMotorChamber().getVolume());
        constants.put(dto.name(), solidRocketMotor.getThroatDiameter());
        constants.put(erate.name(), solidRocketMotor.getNozzleErosion());
        constants.put(two.name(), twoValue);
        constants.put(gstar.name(), solidRocketMotor.getErosiveBurningArea());

        //TODO
        PropellantType propellantType = solidRocketMotor.getPropellantGrain().getPropellantType();
        constants.put(kv.name(), 0d);
        constants.put(pbd.name(), 0d);
        constants.put(rat.name(), 8314/ propellantType.getEffectiveMolecularWeight());
         constants.put(to.name(), propellantType.getChamberTemperature());
         constants.put(patm.name(), 0.101);
         constants.put(k.name(), propellantType.getK());
         constants.put(propellantId.name(), new Double(propellantType.getId()));
         constants.put(rhopgrain.name(), propellantType.getIdealMassDensity()*.95);
        return constants;
    }
}