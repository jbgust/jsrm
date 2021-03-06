package com.github.jbgust.jsrm.infra.performance.function;

import com.github.jbgust.jsrm.infra.function.NaNThrowExceptionFunction;

import static java.lang.Math.pow;

public class NozzleExitPressureFunction extends NaNThrowExceptionFunction {
    //EXCEL FUNCTION
    //=IF(C28/(1+(k2ph-1)/2*N28^2)^(k2ph/(k2ph-1))<patm*1000000;
    //          patm*1000000;
    //          C28/(1+(k2ph-1)/2*N28^2)^(k2ph/(k2ph-1)))

    // NozzleExitPressure(CHAMBER_PRESSURE_MPA, k2ph, MACH_SPEED_AT_NOZZLE_EXIT, patm)
    public NozzleExitPressureFunction() {
        super("NozzleExitPressure", 4);
    }

    @Override
    public double runFunction(double... doubles) {
        double chamberPressureMpa = doubles[0];
        double k2ph = doubles[1];
        double machSpeedAtNozzleExit = doubles[2];
        double patm = doubles[3];

        //mysterious value : C28/(1+(k2ph-1)/2*N28^2)^(k2ph/(k2ph-1))
        double mysteriousValue= chamberPressureMpa/pow(1+(k2ph-1)/2*pow(machSpeedAtNozzleExit,2),(k2ph/(k2ph-1)));

        return mysteriousValue < patm*1000000 ? patm*1000000 : mysteriousValue;
    }
}
