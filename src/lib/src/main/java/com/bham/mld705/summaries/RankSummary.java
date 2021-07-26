package com.bham.mld705.summaries;

public interface RankSummary<S extends RankSummary<S>> extends MultisetSummary<S> {

    public abstract int getRank(int item);

}
