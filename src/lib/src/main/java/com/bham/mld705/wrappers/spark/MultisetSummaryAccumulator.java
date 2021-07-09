package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.MultisetSummary;

import scala.Tuple2;

/**
 * An abstract class that extends {@code SummaryAccumulator} to wrap
 * {@code MultisetSummary}. This adapts a {@code MultisetSummary} for use as a
 * shared variable in a parallel operation on an Apache Spark resilient
 * distributed dataset.
 *
 * @author Martin de Spirlet
 * @see SummaryAccumulator
 * @see MultisetSummary
 */
public abstract class MultisetSummaryAccumulator<S extends MultisetSummary<S>>
        extends SummaryAccumulator<Tuple2<Integer, Integer>, S> {

    /**
     * Wraps the calling implementation of {@code MultisetSummaryAccumulator} around
     * the given {@code MultisetSummary} during construction.
     *
     * @param multisetSummary the {@code MultisetSummary} to be wrapped by the
     *                        calling implementation of
     *                        {@code MultisetSummaryAccumulator}
     * @see MultisetSummary
     */
    protected MultisetSummaryAccumulator(S multisetSummary) {
        super(multisetSummary);
    }

    /**
     * Updates the {@code MultisetSummary} wrapped by this
     * {@code MultisetSummaryAccumulator} by the given {@code Tuple2} of item and
     * weight.
     *
     * @param v the {@code Tuple2} of item and weight by which to update the
     *          {@code MultisetSummary} wrapped by this
     *          {@code MultisetSummaryAccumulator}
     * @see MultisetSummary
     * @see Tuple2
     */
    @Override
    public void add(Tuple2<Integer, Integer> v) {
        value().update(v._1, v._2);
        setZero(false);
    }

}
