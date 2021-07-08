package com.bham.mld705.summaries;

/**
 * A hash summary of an integer multiset data stream. An object of a class that
 * implements this interface can be queried for a hash value that represents the
 * multiset from which it is built.
 *
 * @author Martin de Spirlet
 * @see MultisetSummary
 * @see Summary
 */
public interface IdentificationSummary<S extends IdentificationSummary<S>> extends MultisetSummary<S> {

    /**
     * Returns a hash value that represents the multiset from which this
     * {@code IdentificationSummary} is built.
     *
     * @return a hash value that represents the multiset from which this
     *         {@code IdentificationSummary} is built
     */
    public abstract long getHashValue();

}
