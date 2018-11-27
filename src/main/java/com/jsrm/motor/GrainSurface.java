package com.jsrm.motor;

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
