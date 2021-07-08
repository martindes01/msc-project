package com.bham.mld705.summaries;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.bham.mld705.util.Math;

public final class Fingerprint implements IdentificationSummary<Fingerprint> {

    private static final long BASE_LOWER_BOUND = 1L;
    private static final long PRIME = 3037000493L;
    private static final long ZERO_HASH_VALUE = 0L;

    private final long base;

    private long hashValue;

    private Fingerprint(Fingerprint fingerprint) {
        this.base = fingerprint.base;
        this.hashValue = fingerprint.hashValue;
    }

    private Fingerprint(long base) {
        this.base = base;
        reset();
    }

    public Fingerprint() {
        this(ThreadLocalRandom.current().nextLong(BASE_LOWER_BOUND, PRIME - 1L));
    }

    @Override
    public synchronized long getHashValue() {
        return hashValue;
    }

    @Override
    public synchronized void update(int item, int weight) {
        try {
            hashValue = (hashValue + weight * Math.raiseNonNegativeModulo(base, item, PRIME)) % PRIME;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot update Fingerprint with negative item: " + item, e);
        }
    }

    @Override
    public synchronized Fingerprint copy() {
        return new Fingerprint(this);
    }

    @Override
    public synchronized void merge(Fingerprint other) {
        if (base != other.base) {
            throw new IllegalArgumentException(
                    "Cannot merge Fingerprints with different bases: " + base + " and " + other.base);
        }

        hashValue = (hashValue + other.hashValue) % PRIME;
    }

    @Override
    public synchronized void reset() {
        hashValue = ZERO_HASH_VALUE;
    }

    @Override
    public synchronized boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Fingerprint)) {
            return false;
        }

        Fingerprint other = (Fingerprint) obj;

        return (base == other.base) && (hashValue == other.hashValue);
    }

    @Override
    public synchronized int hashCode() {
        return Objects.hash(base, hashValue);
    }

    @Override
    public synchronized String toString() {
        return "Fingerprint [base=" + base + ", hashValue=" + hashValue + "]";
    }

}
