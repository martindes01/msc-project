package com.bham.mld705.summaries;

public interface MultisetSummary<S extends MultisetSummary<S>> extends Summary<S> {

    public abstract void update(int item, int weight);

}
