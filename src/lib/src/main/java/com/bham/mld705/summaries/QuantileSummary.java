package com.bham.mld705.summaries;

/**
 * An ordered quantile summary of an integer multiset data stream. An object of
 * a class that implements this interface can be queried for an item that has
 * approximately the given rank in the multiset from which it is built.
 *
 * @author Martin de Spirlet
 * @see MultisetSummary
 * @see Summary
 */
public interface QuantileSummary<S extends QuantileSummary<S>> extends MultisetSummary<S> {

    /**
     * Returns an item that has approximately the given rank in the multiset from
     * which this {@code QuantileSummary} is built.
     *
     * @param rank the rank of the item to get
     * @return an item that has approximately the given rank in the multiset from
     *         which this {@code QuantileSummary} is built.
     */
    public abstract int getItem(int rank);

}
