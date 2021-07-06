package com.bham.mld705.summaries;

public interface Summary<S extends Summary<S>> {

    public abstract S copy();

    public abstract void merge(S other);

    public abstract void reset();

}
