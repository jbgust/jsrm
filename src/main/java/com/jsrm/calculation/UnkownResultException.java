package com.jsrm.calculation;

public class UnkownResultException extends RuntimeException {
    UnkownResultException(Formula formula) {
        super("No result is stored for formula : " + formula.getName());
    }
}
