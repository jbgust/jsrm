package com.jsrm.core;

import net.objecthunter.exp4j.function.Function;

import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

public class FormulaConfiguration {

    static final String MISSING_DEPENDENCY_ERROR = "%s is declared as dependency, but not used in the formula : %s";
    static final String NOT_PREVIOUS_FORMULA_REGEX = "%s(?!_previous)";

    String formula;
    String[] dependencies = new String[0];
    JSRMConstant[]  constants = new JSRMConstant[0];
    String[]  variables = new String[0];
    Function[] functions = new Function[0];

    public FormulaConfiguration(String formula) {
        this.formula = formula;
    }

    public FormulaConfiguration withDependencies(String... dependencies) {
        checkUnusedDependencies(dependencies);

        this.dependencies = dependencies;
        return this;
    }

    public FormulaConfiguration withConstants(JSRMConstant... constants) {
        this.constants = constants;
        return this;
    }

    public FormulaConfiguration withVariables(String... variables) {
        this.variables = variables;
        return this;
    }

    public FormulaConfiguration withFunctions(Function... functions) {
        this.functions = functions;
        return this;
    }

    public String getFormula() {
        return formula;
    }

    public Function[] getFunctions() {
        return functions;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public JSRMConstant[] getConstants() {
        return constants;
    }

    public String[] getVariables() {
        return variables;
    }

    private void checkUnusedDependencies(String[] dependencies) {
        //Check to avoid stackOverflowException by using unnecessary dependencies
        Stream.of(dependencies).forEach(dependency -> checkArgument(compile(format(NOT_PREVIOUS_FORMULA_REGEX, dependency)).matcher(formula).find(),
                format(MISSING_DEPENDENCY_ERROR, dependency, formula)));
    }
}
