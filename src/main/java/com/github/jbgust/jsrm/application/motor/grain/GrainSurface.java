package com.github.jbgust.jsrm.application.motor.grain;

/**
 * To define if a surface is exposed to the combustion or not
 */
public enum GrainSurface {
    EXPOSED(1),
    INHIBITED(0);

    private final int value;

    GrainSurface(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
