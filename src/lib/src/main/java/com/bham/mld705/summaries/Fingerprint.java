package com.bham.mld705.summaries;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.bham.mld705.util.Math;

/**
 * A thread-safe implementation of {@code IdentificationSummary}. This class
 * overrides {@code Object.equals(Object)} and {@code Object.hashCode()} such
 * that if two {@code Fingerprint} objects are equal, there is a high
 * probability that they are built from the same multiset. If they are not
 * equal, they must be built from different multisets. For two
 * {@code Fingerprint} objects to be equal, they must have the same base and
 * hash value. Additionally, for two {@code Fingerprint} objects to be
 * mergeable, they must have the same base. To ensure that this is the case, an
 * existing {@code Fingerprint} can be copied using {@code copy()} and reset
 * using {@code reset()}.
 *
 * @author Martin de Spirlet
 * @see #copy()
 * @see #equals(Object)
 * @see #hashCode()
 * @see #merge(Fingerprint)
 * @see #reset()
 * @see IdentificationSummary
 * @see MultisetSummary
 * @see Summary
 */
public final class Fingerprint implements IdentificationSummary<Fingerprint> {

    private static final long BASE_LOWER_BOUND = 1L;
    private static final long PRIME = 3037000493L;
    private static final long ZERO_HASH_VALUE = 0L;

    private final long BASE;

    private long hashValue;

    /**
     * Constructs a copy of the given {@code Fingerprint}.
     *
     * @param fingerprint the {@code Fingerprint} to copy
     */
    private Fingerprint(Fingerprint fingerprint) {
        BASE = fingerprint.BASE;

        hashValue = fingerprint.hashValue;
    }

    /**
     * Constructs a new {@code Fingerprint} with the given base.
     *
     * @param base the base of the new {@code Fingerprint}
     */
    private Fingerprint(long base) {
        BASE = base;

        reset();
    }

    /**
     * Constructs a new {@code Fingerprint} with a randomly chosen base.
     */
    public Fingerprint() {
        this(ThreadLocalRandom.current().nextLong(BASE_LOWER_BOUND, PRIME - 1L));
    }

    /**
     * Returns a hash value that represents the multiset from which this
     * {@code Fingerprint} is built. Note that the hash value returned by this
     * method is almost always different from the hash code returned by
     * {@code hashCode()}.
     *
     * @return a hash value that represents the multiset from which this
     *         {@code Fingerprint} is built
     * @see #equals(Object)
     * @see #hashCode()
     */
    @Override
    public synchronized long getHashValue() {
        return hashValue;
    }

    /**
     * Updates the representation of the given non-negative item in this
     * {@code Fingerprint} by the corresponding weight.
     *
     * @param item   the non-negative item to update
     * @param weight the weight by which to update the item
     * @throws IllegalArgumentException if the item is negative
     */
    @Override
    public synchronized void update(int item, int weight) {
        try {
            hashValue = (hashValue + weight * Math.raiseNonNegativeModulo(BASE, item, PRIME)) % PRIME;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot update Fingerprint with negative item: " + item, e);
        }
    }

    /**
     * Returns a defensive copy of this {@code Fingerprint}. This includes its base
     * and hash value.
     *
     * @return a defensive copy of this {@code Fingerprint}
     */
    @Override
    public synchronized Fingerprint copy() {
        return new Fingerprint(this);
    }

    /**
     * Merges the given {@code Fingerprint} into this {@code Fingerprint}. For two
     * {@code Fingerprint} objects to be merged, they must have the same base. To
     * ensure that this is the case, an existing {@code Fingerprint} can be copied
     * using {@code copy()} and reset using {@code reset()}.
     *
     * @param other the {@code Fingerprint} to merge into this {@code Fingerprint}
     * @throws IllegalArgumentException if the other {@code Fingerprint} does not
     *                                  have the same base as this
     *                                  {@code Fingerprint}
     * @see #copy()
     * @see #reset()
     */
    @Override
    public synchronized void merge(Fingerprint other) {
        if (BASE != other.BASE) {
            throw new IllegalArgumentException(
                    "Cannot merge Fingerprints with different bases: " + this + " and " + other);
        }

        hashValue = (hashValue + other.hashValue) % PRIME;
    }

    /**
     * Resets this {@code Fingerprint} to its initial state. This sets its hash
     * value to zero.
     */
    @Override
    public synchronized void reset() {
        hashValue = ZERO_HASH_VALUE;
    }

    /**
     * Compares this {@code Fingerprint} to the given {@code Object} and returns
     * {@code true} if they represent the same multiset, {@code false} otherwise. To
     * represent the same multiset, the {@code Object} must be a
     * {@code Fingerprint}, and both {@code Fingerprint} objects must have the same
     * base and hash value.
     *
     * @param obj the {@code Object} to compare to this {@code Fingerprint}
     * @return {@code true} if this {@code Fingerprint} and the given {@code Object}
     *         represent the same multiset, {@code false} otherwise
     * @see #getHashValue()
     * @see #hashCode()
     */
    @Override
    public synchronized boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Fingerprint)) {
            return false;
        }

        Fingerprint other = (Fingerprint) obj;

        return (BASE == other.BASE) && (hashValue == other.hashValue);
    }

    /**
     * Returns a hash code calculated from the base and hash value of this
     * {@code Fingerprint}. Note that the hash code returned by this method is
     * almost always different from the hash value returned by
     * {@code getHashValue()}.
     *
     * @return a hash code calculated from the base and hash value of this
     *         {@code Fingerprint}
     * @see #equals(Object)
     * @see #getHashValue()
     */
    @Override
    public synchronized int hashCode() {
        return Objects.hash(BASE, hashValue);
    }

    /**
     * Returns a {@code String} that represents this {@code Fingerprint}.
     *
     * @return a {@code String} that represents this {@code Fingerprint}
     */
    @Override
    public synchronized String toString() {
        return "Fingerprint [BASE=" + BASE + ", hashValue=" + hashValue + "]";
    }

}
