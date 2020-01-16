package com.github.jbgust.jsrm.application.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ChamberPressureOutOfBoundExceptionTest {

    @Test
    public void test() {
        assertThat(new ChamberPressureOutOfBoundException("error").getMessage())
                .isEqualTo("error");
    }

}
