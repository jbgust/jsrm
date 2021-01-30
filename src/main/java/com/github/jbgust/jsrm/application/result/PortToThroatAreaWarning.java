package com.github.jbgust.jsrm.application.result;

/**
 * Port/Throat Area warning :
 * If it drops below 1.0 you will have a DANGER indicator.
 * If Port/Throat Area drops below 2.0 you will have a WARNING indicator.
 * Else return NORMAL indictator
 * Enlarging the core on the nozzle-end grain is a good way to solve this problem.
 * Except for extreme L/D ratio motors, no part of the core should be less than the diameter of the nozzle throat.
 */
public enum PortToThroatAreaWarning {
    NORMAL,
    WARNING,
    DANGER;

    public static PortToThroatAreaWarning fromPortToThroat(double portToThroatArea) {
        if(portToThroatArea < 1D) {
            return DANGER;
        } else if(portToThroatArea < 2D) {
            return WARNING;
        } else {
            return NORMAL;
        }
    }
}
