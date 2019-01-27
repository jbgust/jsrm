package com.github.jbgust.jsrm.infra;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;

public class SolidRocketMotorChecker {

    public static void check(SolidRocketMotor solidRocketMotor) {

        PropellantGrain propellantGrain = solidRocketMotor.getPropellantGrain();
        CombustionChamber combustionChamber = solidRocketMotor.getCombustionChamber();

        if(propellantGrain.getCoreDiameter() < solidRocketMotor.getThroatDiameterInMillimeter()){
            throw new InvalidMotorDesignException("Throat diameter should be >= than grain core diameter");
        }

        if(propellantGrain.getOuterDiameter() > combustionChamber.getChamberInnerDiameterInMillimeter()) {
            throw new InvalidMotorDesignException("Combution chamber diameter should be >= than grain outer diameter");
        }

        if(propellantGrain.getOuterDiameter() <= propellantGrain.getCoreDiameter()) {
            throw new InvalidMotorDesignException("Grain outer diameter should be > than grain core diameter");
        }

        if(propellantGrain.getGrainLength() > combustionChamber.getChamberLengthInMillimeter()) {
            throw new InvalidMotorDesignException("Combustion chamber length should be >= than Grain total length");
        }
    }
}
