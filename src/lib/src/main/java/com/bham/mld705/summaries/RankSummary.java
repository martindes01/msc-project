package com.bham.mld705.summaries;

/**
 * An ordered rank summary of an integer multiset data stream. An object of a
 * class that implements this interface can be queried for the approximate rank
 * of a given item in the multiset from which it is built.
 *
 * @author Martin de Spirlet
 * @see MultisetSummary
 * @see Summary
 */
public interface RankSummary<S extends RankSummary<S>> extends MultisetSummary<S> {

    /**
     * Returns the approximate rank of the given item in the multiset from which
     * this {@code RankSummary} is built.
     *
     * @param item the item whose rank to get
     * @return the approximate rank of the given item in the multiset from which
     *         this {@code RankSummary} is built.
     */
    public abstract int getRank(int item);

}
