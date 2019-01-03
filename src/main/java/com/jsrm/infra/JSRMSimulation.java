package com.jsrm.infra;

import com.jsrm.motor.SolidRocketMotor;

public class JSRMSimulation {

    private final SolidRocketMotor motor;
    private JSRMConfig config;

    public JSRMSimulation(SolidRocketMotor motor) {
        this.motor = motor;

        //TODO, config de base
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
