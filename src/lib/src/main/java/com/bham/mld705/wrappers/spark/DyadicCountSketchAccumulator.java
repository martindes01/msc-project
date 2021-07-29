package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.DyadicCountSketch;

import org.apache.spark.util.AccumulatorV2;

import scala.Tuple2;

public final class DyadicCountSketchAccumulator extends MultisetSummaryAccumulator<DyadicCountSketch> {

    private DyadicCountSketchAccumulator(DyadicCountSketch dyadicCountSketch) {
        super(dyadicCountSketch);
    }

    public DyadicCountSketchAccumulator(int rows, int columns) {
        super(new DyadicCountSketch(rows, columns));
        setZero(true);
    }

    @Override
    public AccumulatorV2<Tuple2<Integer, Integer>, DyadicCountSketch> copy() {
        return new DyadicCountSketchAccumulator(value().copy());
    }

}
