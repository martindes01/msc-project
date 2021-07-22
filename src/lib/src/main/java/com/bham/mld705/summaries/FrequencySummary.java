package com.bham.mld705.summaries;

/**
 * A frequency summary of an integer multiset data stream. An object of a class
 * that implements this interface can be queried for the approximate frequency
 * of a given item in the multiset from which it is built.
 *
 * @author Martin de Spirlet
 * @see MultisetSummary
 * @see Summary
 */
public interface FrequencySummary<S extends FrequencySummary<S>> extends MultisetSummary<S> {

    /**
     * Returns the approximate frequency of the given item in the multiset from
     * which this {@code FrequencySummary} is built.
     *
     * @param item the item whose frequency to get
     * @return the approximate frequency of the given item in the multiset from
     *         which this {@code FrequencySummary} is built
     */
    public abstract int getFrequency(int item);

}
