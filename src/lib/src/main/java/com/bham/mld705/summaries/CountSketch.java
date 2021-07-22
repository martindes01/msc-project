package com.bham.mld705.summaries;

import java.util.Arrays;

import com.bham.mld705.util.Math;
import com.bham.mld705.util.hashfunctions.HashFunction;
import com.bham.mld705.util.hashfunctions.LinearHashFunction;

public final class CountSketch implements FrequencySummary<CountSketch> {

    private static final int PRIME = Integer.MAX_VALUE;
    private static final int SIGN_OFFSET = -1;
    private static final int SIGN_RANGE = 2;

    private final int COLUMNS;
    private final int ROWS;

    private final HashFunction[] DISCRIMINATOR_FUNCTIONS;
    private final HashFunction[] HASH_FUNCTIONS;

    private int[][] sketch;

    private CountSketch(CountSketch countSketch) {
        COLUMNS = countSketch.COLUMNS;
        ROWS = countSketch.ROWS;

        HASH_FUNCTIONS = Arrays.copyOf(countSketch.HASH_FUNCTIONS, ROWS);
        DISCRIMINATOR_FUNCTIONS = Arrays.copyOf(countSketch.DISCRIMINATOR_FUNCTIONS, ROWS);

        sketch = Arrays.stream(countSketch.sketch).map(int[]::clone).toArray(int[][]::new);
    }

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

    @Override
    public synchronized void update(int item, int weight) {
        for (int i = 0; i < ROWS; i++) {
            int hashValue = HASH_FUNCTIONS[i].apply(item);
            int signValue = SIGN_RANGE * DISCRIMINATOR_FUNCTIONS[i].apply(item) + SIGN_OFFSET;

            sketch[i][hashValue] += signValue * weight;
        }
    }

    @Override
    public synchronized CountSketch copy() {
        return new CountSketch(this);
    }

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

    @Override
    public synchronized void reset() {
        sketch = new int[ROWS][COLUMNS];
    }

    @Override
    public synchronized String toString() {
        return "CountSketch [ROWS=" + ROWS + ", COLUMNS=" + COLUMNS + ", HASH_FUNCTIONS="
                + Arrays.toString(HASH_FUNCTIONS) + ", DISCRIMINATOR_FUNCTIONS="
                + Arrays.toString(DISCRIMINATOR_FUNCTIONS) + ", sketch=" + Arrays.toString(sketch) + "]";
    }

}
