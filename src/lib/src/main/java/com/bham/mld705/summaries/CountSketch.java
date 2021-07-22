package com.bham.mld705.summaries;

import java.util.Arrays;

import com.bham.mld705.util.Math;
import com.bham.mld705.util.hashfunctions.HashFunction;
import com.bham.mld705.util.hashfunctions.LinearHashFunction;

/**
 * A thread-safe implementation of {@code FrequencySummary}. For two
 * {@code CountSketch} objects to be mergeable, they must have the same number
 * of rows, number of columns, hash functions and discriminator functions. To
 * ensure that this is the case, an existing {@code CountSketch} can be copied
 * using {@code copy()} and reset using {@code reset()}.
 *
 * @author Martin de Spirlet
 * @see #copy()
 * @see #merge(CountSketch)
 * @see #reset()
 * @see FrequencySummary
 * @see MultisetSummary
 * @see Summary
 */
public final class CountSketch implements FrequencySummary<CountSketch> {

    private static final int PRIME = Integer.MAX_VALUE;
    private static final int SIGN_OFFSET = -1;
    private static final int SIGN_RANGE = 2;

    private final int COLUMNS;
    private final int ROWS;

    private final HashFunction[] DISCRIMINATOR_FUNCTIONS;
    private final HashFunction[] HASH_FUNCTIONS;

    private int[][] sketch;

    /**
     * Constructs a copy of the given {@code CountSketch}.
     *
     * @param countSketch the {@code CountSketch} to copy
     */
    private CountSketch(CountSketch countSketch) {
        COLUMNS = countSketch.COLUMNS;
        ROWS = countSketch.ROWS;

        HASH_FUNCTIONS = Arrays.copyOf(countSketch.HASH_FUNCTIONS, ROWS);
        DISCRIMINATOR_FUNCTIONS = Arrays.copyOf(countSketch.DISCRIMINATOR_FUNCTIONS, ROWS);

        sketch = Arrays.stream(countSketch.sketch).map(int[]::clone).toArray(int[][]::new);
    }

    /**
     * Constructs a new {@code CountSketch} with the given number of rows and number
     * of columns.
     *
     * @param rows    the number of rows of the new {@code CountSketch}
     * @param columns the number of columns of the new {@code CountSketch}
     */
    public CountSketch(int rows, int columns) {
        ROWS = rows;
        COLUMNS = columns;

        HASH_FUNCTIONS = new HashFunction[ROWS];
        DISCRIMINATOR_FUNCTIONS = new HashFunction[ROWS];

        for (int i = 0; i < ROWS; i++) {
            HASH_FUNCTIONS[i] = new LinearHashFunction(PRIME, COLUMNS);
            DISCRIMINATOR_FUNCTIONS[i] = new LinearHashFunction(PRIME, SIGN_RANGE);
        }

        reset();
    }

    /**
     * Returns the approximate frequency of the given item in the multiset from
     * which this {@code CountSketch} is built.
     *
     * @param item the item whose frequency to get
     * @return the approximate frequency of the given item in the multiset from
     *         which this {@code CountSketch} is built
     */
    @Override
    public synchronized int getFrequency(int item) {
        int[] frequencies = new int[ROWS];

        for (int i = 0; i < ROWS; i++) {
            int hashValue = HASH_FUNCTIONS[i].apply(item);
            int signValue = SIGN_RANGE * DISCRIMINATOR_FUNCTIONS[i].apply(item) + SIGN_OFFSET;

            frequencies[i] = signValue * sketch[i][hashValue];
        }

        return Math.sortAndGetMedian(frequencies);
    }

    /**
     * Updates the representation of the given item in this {@code CountSketch} by
     * the corresponding weight.
     *
     * @param item   the item to update
     * @param weight the weight by which to update the item
     */
    @Override
    public synchronized void update(int item, int weight) {
        for (int i = 0; i < ROWS; i++) {
            int hashValue = HASH_FUNCTIONS[i].apply(item);
            int signValue = SIGN_RANGE * DISCRIMINATOR_FUNCTIONS[i].apply(item) + SIGN_OFFSET;

            sketch[i][hashValue] += signValue * weight;
        }
    }

    /**
     * Returns a defensive copy of this {@code CountSketch}. This includes its
     * number of rows, number of columns, hash functions, discriminator functions
     * and sketch array.
     *
     * @return a defensive copy of this {@code CountSketch}
     */
    @Override
    public synchronized CountSketch copy() {
        return new CountSketch(this);
    }

    /**
     * Merges the given {@code CountSketch} into this {@code CountSketch}. For two
     * {@code CountSketch} objects to be merged, they must have the same number of
     * rows, number of columns, hash functions and discriminator functions. To
     * ensure that this is the case, an existing {@code CountSketch} can be copied
     * using {@code copy()} and reset using {@code reset()}.
     *
     * @param other the {@code CountSketch} to merge into this {@code CountSketch}
     * @throws IllegalArgumentException if the other {@code CountSketch} does not
     *                                  have the same number of rows, number of
     *                                  columns, hash functions and discriminator
     *                                  functions as this {@code CountSketch}
     * @see #copy()
     * @see #reset()
     */
    @Override
    public synchronized void merge(CountSketch other) {
        if ((ROWS != other.ROWS) || (COLUMNS != other.COLUMNS) || !Arrays.equals(HASH_FUNCTIONS, other.HASH_FUNCTIONS)
                || !Arrays.equals(DISCRIMINATOR_FUNCTIONS, other.DISCRIMINATOR_FUNCTIONS)) {
            throw new IllegalArgumentException(
                    "Cannot merge CountSketches with different numbers of rows, numbers of columns, hash functions or discriminator functions: "
                            + this + " and " + other);
        }

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                sketch[i][j] += other.sketch[i][j];
            }
        }
    }

    /**
     * Resets this {@code CountSketch} to its initial state. This sets its sketch
     * array elements to zero.
     */
    @Override
    public synchronized void reset() {
        sketch = new int[ROWS][COLUMNS];
    }

    /**
     * Returns a {@code String} that represents this {@code CountSketch}.
     *
     * @return a {@code String} that represents this {@code CountSketch}
     */
    @Override
    public synchronized String toString() {
        return "CountSketch [ROWS=" + ROWS + ", COLUMNS=" + COLUMNS + ", HASH_FUNCTIONS="
                + Arrays.toString(HASH_FUNCTIONS) + ", DISCRIMINATOR_FUNCTIONS="
                + Arrays.toString(DISCRIMINATOR_FUNCTIONS) + ", sketch=" + Arrays.toString(sketch) + "]";
    }

}
