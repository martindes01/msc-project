package com.bham.mld705.summaries;

/**
 * A summary of a data stream. An object of a class that implements this
 * interface can be copied, merged with another {@code Summary} or reset.
 *
 * @author Martin de Spirlet
 */
public interface Summary<S extends Summary<S>> {

    /**
     * Returns a defensive copy of this {@code Summary}.
     *
     * @return a defensive copy of this {@code Summary}
     */
    public abstract S copy();

    /**
     * Merges the given {@code Summary} into this {@code Summary}.
     *
     * @param other the {@code Summary} to merge into this {@code Summary}
     */
    public abstract void merge(S other);

    /**
     * Resets this {@code Summary} to its initial state.
     */
    public abstract void reset();

}
