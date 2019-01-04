package com.jsrm.application.exception;

import com.jsrm.calculation.Formula;

public class UnknownResultException extends RuntimeException {
    public UnknownResultException(Formula formula) {
        super("No result is stored for formula : " + formula.getName());
    }
}
