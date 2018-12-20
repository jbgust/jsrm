package com.jsrm.infra;

import static com.jsrm.core.JSRMConstant.ci;
import static com.jsrm.core.JSRMConstant.dc;
import static com.jsrm.core.JSRMConstant.dto;
import static com.jsrm.core.JSRMConstant.ei;
import static com.jsrm.core.JSRMConstant.erate;
import static com.jsrm.core.JSRMConstant.gstar;
import static com.jsrm.core.JSRMConstant.n;
import static com.jsrm.core.JSRMConstant.osi;
import static com.jsrm.core.JSRMConstant.two;
import static com.jsrm.core.JSRMConstant.vc;
import static com.jsrm.core.JSRMConstant.xincp;

import java.util.HashMap;
import java.util.Map;

import com.jsrm.motor.SolidRocketMotor;

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
        return constants;
    }
}