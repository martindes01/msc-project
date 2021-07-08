package com.bham.mld705.summaries;

public interface IdentificationSummary<S extends IdentificationSummary<S>> extends MultisetSummary<S> {

    public abstract long getHashValue();

}
