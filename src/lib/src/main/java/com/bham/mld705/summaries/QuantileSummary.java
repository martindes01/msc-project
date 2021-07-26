package com.bham.mld705.summaries;

public interface QuantileSummary<S extends QuantileSummary<S>> extends MultisetSummary<S> {

    public abstract int getItem(int rank);

}
