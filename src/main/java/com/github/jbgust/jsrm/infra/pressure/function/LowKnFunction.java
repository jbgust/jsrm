package com.github.jbgust.jsrm.infra.pressure.function;

import com.github.jbgust.jsrm.infra.function.NaNThrowExceptionFunction;

public class LowKnFunction extends NaNThrowExceptionFunction {

    public static final double LOW_KN_MASS_STORAGE_RATE = 0.0001;

    public LowKnFunction() {
        super("lowKn", 2);
    }

    @Override
    public double runFunction(double... doubles) {
        double useSafeKn = doubles[1];
        double massStorageRateResult = doubles[0];

        if(useSafeKn == 1d && massStorageRateResult < 0.0){
            return LOW_KN_MASS_STORAGE_RATE;
        } else {
            return massStorageRateResult;
        }
    }
}
