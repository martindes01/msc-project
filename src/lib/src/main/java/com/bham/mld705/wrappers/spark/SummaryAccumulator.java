package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.Summary;

import org.apache.spark.util.AccumulatorV2;

public abstract class SummaryAccumulator<U, S extends Summary<S>> extends AccumulatorV2<U, S> {

    private S summary;

    private boolean zero;

    protected SummaryAccumulator(S summary) {
        this.summary = summary;
    }

    protected void setZero(boolean zero) {
        this.zero = zero;
    }

    @Override
    public boolean isZero() {
        return zero;
    }

    @Override
    public void merge(AccumulatorV2<U, S> other) {
        value().merge(other.value());
        setZero(false);
    }

    @Override
    public void reset() {
        value().reset();
        setZero(true);
    }

    @Override
    public S value() {
        return summary;
    }

}
