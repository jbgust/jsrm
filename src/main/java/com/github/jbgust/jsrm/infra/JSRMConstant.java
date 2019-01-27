package com.github.jbgust.jsrm.infra;

public enum JSRMConstant {
    ci,
    xincp,
    osi,
    ei,
    n,
    dto,
    erate,
    two,
    dc,
    vc,
    propellantId,
    mgrain,
    k2ph,
    etanoz,
    gstar,
    kv,
    pbd,
    rat,
    to,
    patm,
    k,
    cstar,
    at,
    rhopgrain,

    expectedPfinal(true),
    pbout(true),
    me(true),
    mef(true),
    atfinal(true),
    aexit(true),
    tbinc(true),
    tbout(true),
    astarf(true);

    public static final int UNIVERSAL_GAS_CONSTANT = 8314; //unit [J/mol-K]
    public static final double GRAVITATIONAL_ACCELERATION = 9.806;
    public static final double PMAXPERC = 0.02;
    public static final double PBD = 0.0;

    public static final int NUMBER_LINE_DURING_BURN_CALCULATION = 835;
    public static final int NUMBER_LINE_DURING_POST_BURN_CALCULATION = 47;
    public static final int LAST_CALCULATION_LINE = (int) (NUMBER_LINE_DURING_BURN_CALCULATION + NUMBER_LINE_DURING_POST_BURN_CALCULATION);
    public static final int START_CALCULATION_LINE = 0;
    private final boolean constantExtractedDuringCalculation;

    JSRMConstant(boolean constantExtractedDuringCalculation) {
        this.constantExtractedDuringCalculation = constantExtractedDuringCalculation;
    }

    JSRMConstant() {
        this(false);
    }

    public boolean isConstantExtractedDuringCalculation() {
        return constantExtractedDuringCalculation;
    }
}
