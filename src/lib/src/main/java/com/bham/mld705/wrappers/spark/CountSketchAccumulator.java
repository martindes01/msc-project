package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.CountSketch;

import org.apache.spark.util.AccumulatorV2;

import scala.Tuple2;

/**
 * A class that extends {@code MultisetSummaryAccumulator} to wrap
 * {@code CountSketch}. This adapts a {@code CountSketch} for use as a shared
 * variable in a parallel operation on an Apache Spark resilient distributed
 * dataset.
 *
 * @author Martin de Spirlet
 * @see MultisetSummaryAccumulator
 * @see SummaryAccumulator
 * @see CountSketch
 */
public final class CountSketchAccumulator extends MultisetSummaryAccumulator<CountSketch> {

    /**
     * Constructs a new {@code CountSketchAccumulator} that wraps the given
     * {@code CountSketch}.
     *
     * @param countSketch the {@code CountSketch} to be wrapped by the new
     *                    {@code CountSketchAccumulator}
     */
    private CountSketchAccumulator(CountSketch countSketch) {
        super(countSketch);
    }

    /**
     * Constructs a new {@code CountSketchAccumulator} that wraps a new
     * {@code CountSketch} with the given number of rows and number of columns.
     *
     * @param rows    the number of rows of the new {@code CountSketch} to be
     *                wrapped by the new {@code CountSketchAccumulator}
     * @param columns the number of columns of the new {@code CountSketch} to be
     *                wrapped by the new {@code CountSketchAccumulator}
     */
    public CountSketchAccumulator(int rows, int columns) {
        super(new CountSketch(rows, columns));
        setZero(true);
    }

    /**
     * Returns a defensive copy of this {@code CountSketchAccumulator}.
     *
     * @return a defensive copy of this {@code CountSketchAccumulator}
     */
    @Override
    public AccumulatorV2<Tuple2<Integer, Integer>, CountSketch> copy() {
        return new CountSketchAccumulator(value().copy());
    }

}
