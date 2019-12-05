package com.github.jbgust.jsrm.infra;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.RegisteredPropellant;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
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

        SolidPropellant propellant = propellantGrain.getPropellant();

        HashMap<JSRMConstant, Double> constants = new HashMap<>();
        constants.put(dc, solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter());
        constants.put(vc, solidRocketMotor.getCombustionChamber().getVolume());
        constants.put(dto, solidRocketMotor.getThroatDiameterInMillimeter());
        constants.put(at, new CircleAreaFunction().runFunction(solidRocketMotor.getThroatDiameterInMillimeter()));
        constants.put(safeKN, config.isSafeKNFailure() ? 1d : 0d);

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

        constants.put(xincp, propellantGrain.getGrainConfigutation().getXincp(config.getNumberLineDuringBurnCalculation()));

        return constants;
    }

    private static double computeGrainMass(Double rhopgrain, PropellantGrain propellantGrain) {
        return propellantGrain.getGrainConfigutation().getGrainVolume(0) * rhopgrain / 1000 / 1000;
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
