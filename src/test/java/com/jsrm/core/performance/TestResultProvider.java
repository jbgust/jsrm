package com.jsrm.core.performance;

import com.jsrm.calculation.ResultLineProvider;

import java.util.Map;

class TestResultProvider implements ResultLineProvider {

    private final String name;
    private Map<String, Double> csvData;

    TestResultProvider(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getResult(int lineNumber) {
        return csvData.get(name);
    }

    public void setCsvData(Map<String, Double> csvData){
        this.csvData = csvData;
    }

}
