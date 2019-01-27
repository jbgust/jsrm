package com.github.jbgust.jsrm.calculation;

public interface ResultLineProvider {

    String getName();
    double getResult(int lineNumber);
}
