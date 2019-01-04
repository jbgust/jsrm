package com.jsrm.application;

import com.jsrm.application.result.JSRMResult;
import com.jsrm.application.motor.SolidRocketMotor;

public class JSRMSimulation {

    private final SolidRocketMotor motor;
    private JSRMConfig config;

    public JSRMSimulation(SolidRocketMotor motor) {
        this.motor = motor;
        config = JSRMConfig.builder().build();
    }

    public JSRMResult run(JSRMConfig config) {
        return null;
    }

    public JSRMResult run() {
        return run(config);
    }

    //TODO: nozzle desing result

}
