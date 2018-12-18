package com.jsrm.motor.propellant;

public class KNDX implements SolidPropellant {

    @Override
    public String getDescription() {
        return "KNDX = Potassium Nitrate/Dextrose, 65/35 O/F ratio\n";
    }

    @Override
    public double getIdealMassDensity() {
        return 1.879;
    }

    @Override
    public double getK2Ph() {
        return 1.043;
    }

    @Override
    public double getK() {
        return 1.131;
    }

    @Override
    public double getEffectiveMolecularWeight() {
        return 42.39;
    }

    @Override
    public double getChamberTemperature() {
        return 1710d;
    }

    @Override
    public double getBurnRateCoefficient(double chamberPressure) throws ChamberPressureOutOfBoundException {

        //TODO : https://stackoverflow.com/questions/7976744/how-do-you-find-if-a-number-is-within-a-range-in-java-problems-with-math-absnu
        if (chamberPressure >= 0.1 &&  chamberPressure < 0.779135){
            return 8.87544496778536;
        } else if (chamberPressure >= 0.779135 &&  chamberPressure < 2.571835){
            return 7.55278442387944;
        } else if (chamberPressure >= 2.571835 &&  chamberPressure < 5.9297){
            return 3.84087990499602;
        } else if (chamberPressure >= 5.9297 &&  chamberPressure < 8.501535){
            return 17.2041864098062;
        } else if (chamberPressure >= 8.501535 ){
            //According to Richard Nakka burnrate Excel sheet this value is for the range 8,501535 to 11,204375
            // but the algorithm return also this value is the pressure exceed 11,204375 MPa
            return 4.77524086347659;
        } else {
            throw new ChamberPressureOutOfBoundException("No burn rate coefficient for this pressure ("+chamberPressure+") should be in range [0.1;+infinite[");
        }
    }

    @Override
    public double getPressureExponent(double chamberPressure) throws ChamberPressureOutOfBoundException {
        //TODO : https://stackoverflow.com/questions/7976744/how-do-you-find-if-a-number-is-within-a-range-in-java-problems-with-math-absnu
        if (chamberPressure >= 0.1 &&  chamberPressure < 0.779135){
            return 0.6193;
        } else if (chamberPressure >= 0.779135 &&  chamberPressure < 2.571835){
            return -0.0087;
        } else if (chamberPressure >= 2.571835 &&  chamberPressure < 5.9297){
            return 0.6882;
        } else if (chamberPressure >= 5.9297 &&  chamberPressure < 8.501535){
            return -0.1481;
        } else if (chamberPressure >= 8.501535 ){
            //According to Richard Nakka burnrate Excel sheet this value is for the range 8,501535 to 11,204375
            // but the algorithm return also this value is the pressure exceed 11,204375 MPa
            return 0.4417;
        } else {
            throw new ChamberPressureOutOfBoundException("No pressure exponent for this pressure ("+chamberPressure+") should be in range [0.1;+infinite[");
        }
    }
}
