package com.github.jbgust.jsrm.infra;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.infra.function.CircleAreaFunction;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.github.jbgust.jsrm.infra.JSRMConstant.*;
import static java.util.stream.Collectors.toMap;

public class ConstantsExtractor {

    public static Map<JSRMConstant, Double> extract(SolidRocketMotor solidRocketMotor, JSRMConfig config) {
        PropellantGrain propellantGrain = solidRocketMotor.getPropellantGrain();

        double twoValue = (propellantGrain.getOuterDiameter() - propellantGrain.getCoreDiameter()) / 2;
        SolidPropellant propellant = propellantGrain.getPropellant();

        HashMap<JSRMConstant, Double> constants = new HashMap<>();
        constants.put(ci, (double) propellantGrain.getCoreSurface().value());
        constants.put(osi, (double) propellantGrain.getOuterSurface().value());
        constants.put(ei, (double) propellantGrain.getEndsSurface().value());
        constants.put(xincp, twoValue / (NUMBER_LINE_DURING_BURN_CALCULATION - 1) / getXincFactor(propellantGrain));
        constants.put(dc, solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter());
        constants.put(n, propellantGrain.getNumberOfSegment());
        constants.put(vc, solidRocketMotor.getCombustionChamber().getVolume());
        constants.put(dto, solidRocketMotor.getThroatDiameterInMillimeter());
        constants.put(two, twoValue);
        constants.put(at, new CircleAreaFunction().runFunction(solidRocketMotor.getThroatDiameterInMillimeter()));

        constants.put(erate, config.getNozzleErosionInMillimeter());
        constants.put(gstar, config.getErosiveBurningAreaRatioThreshold());
        constants.put(kv, config.getErosiveBurningVelocityCoefficient());
        constants.put(to, config.getCombustionEfficiencyRatio() * propellant.getChamberTemperature());
        constants.put(patm, config.getAmbiantPressureInMPa());
        constants.put(rhopgrain, config.getDensityRatio()*propellant.getIdealMassDensity());
        constants.put(etanoz, config.getNozzleEfficiency());

        constants.put(pbd, PBD);
        constants.put(rat, UNIVERSAL_GAS_CONSTANT / propellant.getEffectiveMolecularWeight());
        constants.put(k, propellant.getK());
        constants.put(propellantId, Double.valueOf(RegisteredPropellant.registerPropellant(propellant)));
        constants.put(cstar, computeCstarValue(constants));
        constants.put(k2ph, propellant.getK2Ph());
        constants.put(mgrain, computeGrainMass(constants.get(rhopgrain), propellantGrain));

        return constants;
    }

    private static int getXincFactor(PropellantGrain propellantGrain) {
        return propellantGrain.getCoreSurface().value() + propellantGrain.getOuterSurface().value();
    }

    private static double computeGrainMass(Double rhopgrain, PropellantGrain propellantGrain) {
        return new ExpressionBuilder("pi/4*(do^2-dio^2)*lgo")
                .variables("do", "dio", "lgo")
                .build()
                .setVariable("do", propellantGrain.getOuterDiameter())
                .setVariable("dio", propellantGrain.getCoreDiameter())
                .setVariable("lgo", propellantGrain.getGrainLength())
                .evaluate() * rhopgrain / 1000 / 1000;
    }

    private static double computeCstarValue(HashMap<JSRMConstant, Double> constants) {
        return new ExpressionBuilder("sqrt(rat*to/k*((k+1)/2)^((k+1)/(k-1)))")
                    .variables(rat.name(), to.name(), k.name())
                    .build()
                    .setVariables(toCalculationFormat(constants))
                    .evaluate();
    }

    public static Map<String, Double> toCalculationFormat(Map<JSRMConstant, Double> jsrmConstantDoubleMap) {
        return jsrmConstantDoubleMap.entrySet().stream()
                .collect(toMap(entry -> entry.getKey().name(), Map.Entry::getValue));
    }
}