package com.github.jbgust.jsrm.application.result;

import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.result.PortToThroatAreaWarning.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class PortToThroatAreaWarningTest {

    @Test
    void shouldReturnWARNING() {
        assertThat(fromPortToThroat(1.99D)).isEqualTo(WARNING);
        assertThat(fromPortToThroat(1.1D)).isEqualTo(WARNING);
    }

    @Test
    void shouldReturnDANGER() {
        assertThat(fromPortToThroat(0.99D)).isEqualTo(DANGER);
        assertThat(fromPortToThroat(0.1D)).isEqualTo(DANGER);
    }

    @Test
    void shouldReturnNORMAL() {
        assertThat(fromPortToThroat(2D)).isEqualTo(NORMAL);
    }
}
