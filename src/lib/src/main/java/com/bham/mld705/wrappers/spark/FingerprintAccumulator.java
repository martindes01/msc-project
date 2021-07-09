package com.bham.mld705.wrappers.spark;

import com.bham.mld705.summaries.Fingerprint;

import org.apache.spark.util.AccumulatorV2;

import scala.Tuple2;

/**
 * A class that extends {@code MultisetSummaryAccumulator} to wrap
 * {@code Fingerprint}. This adapts a {@code Fingerprint} for use as a shared
 * variable in a parallel operation on an Apache Spark resilient distributed
 * dataset.
 *
 * @author Martin de Spirlet
 * @see MultisetSummaryAccumulator
 * @see Fingerprint
 */
public final class FingerprintAccumulator extends MultisetSummaryAccumulator<Fingerprint> {

    /**
     * Constructs a new {@code FingerprintAccumulator} that wraps the given
     * {@code Fingerprint}.
     *
     * @param fingerprint the {@code Fingerprint} to be wrapped by the new
     *                    {@code FingerprintAccumulator}
     * @see Fingerprint
     */
    private FingerprintAccumulator(Fingerprint fingerprint) {
        super(fingerprint);
    }

    /**
     * Constructs a new {@code FingerprintAccumulator} that wraps a new
     * {@code Fingerprint}.
     *
     * @see Fingerprint
     */
    public FingerprintAccumulator() {
        super(new Fingerprint());
        setZero(true);
    }

    /**
     * Returns a defensive copy of this {@code Fingerprint}.
     *
     * @return a defensive copy of this {@code Fingerprint}
     * @see Fingerprint
     */
    @Override
    public AccumulatorV2<Tuple2<Integer, Integer>, Fingerprint> copy() {
        return new FingerprintAccumulator(value().copy());
    }

}
