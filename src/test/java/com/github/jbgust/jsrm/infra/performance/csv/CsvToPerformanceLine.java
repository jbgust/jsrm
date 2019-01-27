package com.github.jbgust.jsrm.infra.performance.csv;

import org.junit.jupiter.params.aggregator.AggregateWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AggregateWith(PerformanceCsvLineAggregator.class)
public @interface CsvToPerformanceLine {
}
