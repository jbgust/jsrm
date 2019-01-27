package com.github.jbgust.jsrm.infra.function;

import com.github.jbgust.jsrm.calculation.exception.InvalidResultException;
import net.objecthunter.exp4j.function.Function;

import java.util.Arrays;

import static java.lang.Double.isNaN;

public abstract class NaNThrowExceptionFunction extends Function {

    public NaNThrowExceptionFunction(String name, int numArguments) {
        super(name, numArguments);
    }

    @Override
    public double apply(double... doubles) {
        double result = runFunction(doubles);
        if(isNaN(result)){
            throw new InvalidResultException("Function " + name + " return NaN\n" +
                    "\tparams : "+ Arrays.toString(doubles));
        }
        return result;
    }

    protected abstract double runFunction(double[] doubles);
}
