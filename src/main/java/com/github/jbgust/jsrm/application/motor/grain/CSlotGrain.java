package com.github.jbgust.jsrm.application.motor.grain;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.grain.core.BurningShape;
import com.github.jbgust.jsrm.application.motor.grain.core.ExtrudedShapeGrain;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Bill Kuker
 * https://github.com/bkuker/motorsim
 */
public class CSlotGrain extends ExtrudedShapeGrain {

    /**
     * Grain outer diameter
     */
    private final double outerDiameter;

    /**
     * Grain core diameter (can be 0)
     */
    private final double coreDiameter;

    /**
     * C slot width
     */
    private final double slotWidth;

    /**
     * C slot depth
     */
    private final double slotDepth;

    /**
     * C slot offset
     */
    private final double slotOffset;

    /**
     * Number of segment
     */
    private final int numberOfSegment;

    public CSlotGrain(double outerDiameter, double coreDiameter, double slotWidth, double slotDepth, double slotOffset, int numberOfSegment, double length, GrainSurface endSurface) {
        this.outerDiameter = outerDiameter;
        this.coreDiameter = coreDiameter;
        this.slotWidth = slotWidth;
        this.slotDepth = slotDepth;
        this.slotOffset = slotOffset;
        this.numberOfSegment = numberOfSegment;
        setAftEndInhibited(endSurface);
        setForeEndInhibited(endSurface);
        setLength(length);
        generateGeometry();
    }

    private void generateGeometry() {
        double odmm = outerDiameter;
        double wmm = slotWidth;
        double dmm = slotDepth;
        xsection = new BurningShape();
        Shape outside = new Ellipse2D.Double(0, 0, odmm, odmm);
        xsection.add(outside);
        xsection.inhibit(outside);

        double offmm = slotOffset;

        double ymm = odmm / 2.0 - wmm / 2.0 - offmm; //The Y position of the slot
        double xmm = odmm - dmm; //X pos of slot
        Rectangle2D.Double slot;
        slot = new Rectangle2D.Double(xmm, ymm, dmm, wmm);
        xsection.subtract(slot);

        double idmm = coreDiameter;
        double idymm = odmm / 2.0 - idmm / 2.0 - offmm; //y pos of id
        double idxmm = xmm - idmm / 2.0; //x pos of id
        Ellipse2D.Double id = new Ellipse2D.Double(idxmm, idymm, idmm, idmm);
        xsection.subtract(id);
    }

    @Override
    public int getNumberOfSegments() {
        return numberOfSegment;
    }

    @Override
    public void checkConfiguration(SolidRocketMotor solidRocketMotor) throws InvalidMotorDesignException {
        if (coreDiameter < 0) {
            throw new InvalidMotorDesignException("Core diameter should be positive");
        }
        if (outerDiameter <= 0) {
            throw new InvalidMotorDesignException("Outer diameter should be > 0");
        }
        if (getLength() <= 0) {
            throw new InvalidMotorDesignException("Grain length should be > 0");
        }

        if (numberOfSegment <= 0) {
            throw new InvalidMotorDesignException("Number of segment should be > 0");
        }

        if (coreDiameter >= outerDiameter) {
            throw new InvalidMotorDesignException("Core diameter should be < than outer diameter");
        }
        if (getLength() * getNumberOfSegments() > solidRocketMotor.getCombustionChamber().getChamberLengthInMillimeter()) {
            throw new InvalidMotorDesignException("Combustion chamber length should be >= than Grain total length");
        }
        if (outerDiameter > solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter()) {
            throw new InvalidMotorDesignException("Combution chamber diameter should be >= than grain outer diameter");
        }

        if (slotOffset < 0) {
            throw new InvalidMotorDesignException("Slot offset should be positive");
        }

        if (slotOffset >= outerDiameter / 2) {
            throw new InvalidMotorDesignException("Slot offset should be < than grain radius");
        }

        if (slotDepth <= 0) {
            throw new InvalidMotorDesignException("Slot depth should be > 0");
        }

        if (slotDepth >= outerDiameter) {
            throw new InvalidMotorDesignException("Slot depth should be <= than outer diameter");
        }

        if (slotWidth <= 0) {
            throw new InvalidMotorDesignException("Slot width should be > 0");
        }

        if (slotWidth >= outerDiameter) {
            throw new InvalidMotorDesignException("Slot width should be <= than outer diameter");
        }
    }


}
