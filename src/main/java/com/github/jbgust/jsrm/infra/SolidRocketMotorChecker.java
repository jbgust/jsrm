package com.github.jbgust.jsrm.infra;

import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;

public class SolidRocketMotorChecker {

    public static void check(SolidRocketMotor solidRocketMotor) {
        solidRocketMotor.getPropellantGrain().getGrainConfigutation().checkConfiguration(solidRocketMotor);
    }
}
