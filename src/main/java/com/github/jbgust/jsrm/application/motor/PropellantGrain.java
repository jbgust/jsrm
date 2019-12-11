package com.github.jbgust.jsrm.application.motor;

import com.github.jbgust.jsrm.application.motor.grain.GrainConfigutation;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;

public class PropellantGrain {

    private final SolidPropellant propellant;
    private final GrainConfigutation grainConfigutation;

    /**
     * Create an Hollow cylindrical propellant grain
     * @param propellant propellant used (for default propellant see {@link PropellantType})
     * @param grainConfigutation the grain configuration see {@link GrainConfigutation})
     */
    public PropellantGrain(SolidPropellant propellant, GrainConfigutation grainConfigutation) {
        this.propellant = propellant;
        this.grainConfigutation = grainConfigutation;
    }

    public SolidPropellant getPropellant() {
        return propellant;
    }

    public GrainConfigutation getGrainConfigutation() {
        return grainConfigutation;
    }
}
