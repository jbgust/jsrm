package com.github.jbgust.jsrm.infra.performance.function;

import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.utils.KNDX_SRM_2014;
import com.github.jbgust.jsrm.utils.KNSU_SRM_2014;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NozzleExitPressureFunctionTest {

    @Test
    void shouldReturnPatmValue() {
        double chamberPressureMpa = 190000;
        double k2ph = 23;
        double machSpeedAtNozzleExit = 3;
        double patm = 0.101;

        NozzleExitPressureFunction nozzleExitPressureFunction = new NozzleExitPressureFunction();
        double result = nozzleExitPressureFunction.runFunction(chamberPressureMpa, k2ph, machSpeedAtNozzleExit, patm);

        assertThat(result).isEqualTo(patm*1000000);
    }

    @Test
    void shouldNotReturnPatmValue() {
        double chamberPressureMpa = 2935262.54708905;
        double k2ph = new KNDX_SRM_2014().getK2Ph();
        double machSpeedAtNozzleExit = 2.00813230872116;
        double patm = 0.101;

        NozzleExitPressureFunction nozzleExitPressureFunction = new NozzleExitPressureFunction();
        double result = nozzleExitPressureFunction.runFunction(chamberPressureMpa, k2ph, machSpeedAtNozzleExit, patm);

        assertThat(result).isEqualTo(390633.81, Offset.offset(0.01));
    }

}
