package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.CountSketch;

import org.apache.spark.util.AccumulatorV2;

import scala.Tuple2;

public final class CountSketchAccumulator extends MultisetSummaryAccumulator<CountSketch> {

    private CountSketchAccumulator(CountSketch countSketch) {
        super(countSketch);
    }

    public CountSketchAccumulator(int rows, int columns) {
        super(new CountSketch(rows, columns));
        setZero(true);
    }

    @Override
    public AccumulatorV2<Tuple2<Integer, Integer>, CountSketch> copy() {
        return new CountSketchAccumulator(value().copy());
    }

}
