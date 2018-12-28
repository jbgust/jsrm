package com.jsrm.calculation;

public class UnknownResultException extends RuntimeException {
    UnknownResultException(Formula formula) {
        super("No result is stored for formula : " + formula.getName());
    }
}
