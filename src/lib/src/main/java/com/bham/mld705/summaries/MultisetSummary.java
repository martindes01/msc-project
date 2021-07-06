package com.bham.mld705.summaries;

/**
 * A summary of an integer multiset data stream. An object of a class that
 * implements this interface can update its representation of a given integer
 * item by a corresponding integer weight.
 *
 * @author Martin de Spirlet
 * @see Summary
 */
public interface MultisetSummary<S extends MultisetSummary<S>> extends Summary<S> {

    /**
     * Updates the representation of the given item in this {@code MultisetSummary}
     * by the corresponding weight.
     *
     * @param item   the item to update
     * @param weight the weight by which to update the item
     */
    public abstract void update(int item, int weight);

}
