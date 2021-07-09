package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.Fingerprint;

import org.apache.spark.util.AccumulatorV2;

import scala.Tuple2;

public final class FingerprintAccumulator extends MultisetSummaryAccumulator<Fingerprint> {

    private FingerprintAccumulator(Fingerprint fingerprint) {
        super(fingerprint);
    }

    public FingerprintAccumulator() {
        super(new Fingerprint());
        setZero(true);
    }

    @Override
    public AccumulatorV2<Tuple2<Integer, Integer>, Fingerprint> copy() {
        return new FingerprintAccumulator(value().copy());
    }

}
