package com.bham.mld705.summaries;

public interface FrequencySummary<S extends FrequencySummary<S>> extends MultisetSummary<S> {

    public abstract int getFrequency(int item);

}
