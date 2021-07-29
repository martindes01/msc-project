package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.DyadicCountSketch;

import org.apache.spark.util.AccumulatorV2;

import scala.Tuple2;

/**
 * A class that extends {@code MultisetSummaryAccumulator} to wrap
 * {@code DyadicCountSketch}. This adapts a {@code DyadicCountSketch} for use as
 * a shared variable in a parallel operation on an Apache Spark resilient
 * distributed dataset.
 *
 * @author Martin de Spirlet
 * @see MultisetSummaryAccumulator
 * @see SummaryAccumulator
 * @see DyadicCountSketch
 */
public final class DyadicCountSketchAccumulator extends MultisetSummaryAccumulator<DyadicCountSketch> {

    /**
     * Constructs a new {@code DyadicCountSketchAccumulator} that wraps the given
     * {@code DyadicCountSketch}.
     *
     * @param dyadicCountSketch the {@code DyadicCountSketch} to be wrapped by the
     *                          new {@code DyadicCountSketchAccumulator}
     */
    private DyadicCountSketchAccumulator(DyadicCountSketch dyadicCountSketch) {
        super(dyadicCountSketch);
    }

    /**
     * Constructs a new {@code DyadicCountSketchAccumulator} that wraps a new
     * {@code DyadicCountSketch} whose constituent {@code CountSketch} objects each
     * have the given number of rows and number of columns.
     *
     * @param rows    the number of rows of each constituent {@code CountSketch} of
     *                the new {@code DyadicCountSketch} to be wrapped by the new
     *                {@code DyadicCountSketchAccumulator}
     * @param columns the number of columns of each constituent {@code CountSketch}
     *                of the new {@code DyadicCountSketch} to be wrapped by the new
     *                {@code DyadicCountSketchAccumulator}
     * @see com.bham.mld705.summaries.CountSketch
     */
    public DyadicCountSketchAccumulator(int rows, int columns) {
        super(new DyadicCountSketch(rows, columns));
        setZero(true);
    }

    /**
     * Returns a defensive copy of this {@code DyadicCountSketchAccumulator}.
     *
     * @return a defensive copy of this {@code DyadicCountSketchAccumulator}
     */
    @Override
    public AccumulatorV2<Tuple2<Integer, Integer>, DyadicCountSketch> copy() {
        return new DyadicCountSketchAccumulator(value().copy());
    }

}
