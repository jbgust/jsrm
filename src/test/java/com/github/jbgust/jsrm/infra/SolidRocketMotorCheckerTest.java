package com.github.jbgust.jsrm.infra;

import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.grain.GrainConfigutation;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNER_COARSE;
import static com.github.jbgust.jsrm.infra.SolidRocketMotorChecker.check;
import static org.mockito.Mockito.*;

class SolidRocketMotorCheckerTest {

    @Test
    void shouldCheckSolidRocketMotor(){
        GrainConfigutation grainConfigutation = mock(GrainConfigutation.class);

        SolidRocketMotor solidRocketMotor = new SolidRocketMotor(new PropellantGrain(KNER_COARSE, grainConfigutation), new CombustionChamber(20, 80), 8d);

        check(solidRocketMotor);

        verify(grainConfigutation, times(1)).checkConfiguration(solidRocketMotor);
    }

}
