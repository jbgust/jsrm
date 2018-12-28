package com.jsrm.motor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

class MotorChamberTest {

    @Test
    void shouldComputeChamberVolume() {
        //GIVEN
        int chamberInnerDiameter = 20;
        int ChamberLength = 70;

        //WHEN
        double volume = new MotorChamber(chamberInnerDiameter, ChamberLength).getVolume();

        //THEN
        assertThat(volume).isEqualTo(21991.14857, offset(0.00001d));
    }


}