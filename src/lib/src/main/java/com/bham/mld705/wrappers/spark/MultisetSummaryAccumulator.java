package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.MultisetSummary;

import scala.Tuple2;

public abstract class MultisetSummaryAccumulator<S extends MultisetSummary<S>>
        extends SummaryAccumulator<Tuple2<Integer, Integer>, S> {

    protected MultisetSummaryAccumulator(S multisetSummary) {
        super(multisetSummary);
    }

    @Override
    public void add(Tuple2<Integer, Integer> v) {
        value().update(v._1, v._2);
        setZero(false);
    }

}
