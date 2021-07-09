package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.Summary;

import org.apache.spark.util.AccumulatorV2;

/**
 * An abstract class that extends {@code AccumulatorV2} to wrap {@code Summary}.
 * This adapts a {@code Summary} for use as a shared variable in a parallel
 * operation on an Apache Spark resilient distributed dataset.
 *
 * @author Martin de Spirlet
 * @see AccumulatorV2
 * @see Summary
 */
public abstract class SummaryAccumulator<U, S extends Summary<S>> extends AccumulatorV2<U, S> {

    private S summary;

    private boolean zero;

    /**
     * Wraps the calling implementation of {@code SummaryAccumulator} around the
     * given {@code Summary} during construction.
     *
     * @param summary the {@code Summary} to be wrapped by the calling
     *                implementation of {@code SummaryAccumulator}
     * @see Summary
     */
    protected SummaryAccumulator(S summary) {
        this.summary = summary;
    }

    /**
     * Sets the zero state of this {@code SummaryAccumulator} to the given value.
     *
     * @param zero the value to set as the zero state of this
     *             {@code SummaryAccumulator}
     */
    protected void setZero(boolean zero) {
        this.zero = zero;
    }

    /**
     * Returns whether this {@code SummaryAccumulator} is in its zero state.
     *
     * @return {@code true} if this {@code SummaryAccumulator} is in its zero state,
     *         false otherwise
     */
    @Override
    public boolean isZero() {
        return zero;
    }

    /**
     * Merges the given {@code AccumulatorV2} into this {@code SummaryAccumulator}.
     *
     * @param other the {@code AccumulatorV2} to merge into this
     *              {@code SummaryAccumulator}
     * @see AccumulatorV2
     */
    @Override
    public void merge(AccumulatorV2<U, S> other) {
        value().merge(other.value());
        setZero(false);
    }

    /**
     * Resets this {@code SummaryAccumulator} to its zero state.
     */
    @Override
    public void reset() {
        value().reset();
        setZero(true);
    }

    /**
     * Returns the {@code Summary} wrapped by this {@code SummaryAccumulator}.
     *
     * @return the {@code Summary} wrapped by this {@code SummaryAccumulator}
     * @see Summary
     */
    @Override
    public S value() {
        return summary;
    }

}
