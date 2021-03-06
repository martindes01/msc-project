package com.bham.mld705.summaries;

import java.util.Arrays;

/**
 * A thread-safe implementation of {@code QuantileSummary} and
 * {@code RankSummary}. For two {@code DyadicCountSketch} objects to be
 * mergeable, each corresponding pair of their constituent {@code CountSketch}
 * and {@code FrequencyTable} objects must be mergeable. To ensure that this is
 * the case, an existing {@code DyadicCountSketch} can be copied using
 * {@code copy()} and reset using {@code reset()}.
 *
 * @author Martin de Spirlet
 * @see #copy()
 * @see #merge(DyadicCountSketch)
 * @see #reset()
 * @see FrequencyTable
 * @see QuantileSummary
 * @see RankSummary
 * @see MultisetSummary
 * @see Summary
 * @see CountSketch
 */
public final class DyadicCountSketch implements QuantileSummary<DyadicCountSketch>, RankSummary<DyadicCountSketch> {

    /**
     * An implementation of {@code FrequencySummary} that simply holds a counter for
     * each item in the multiset from which it is built. This class is for use only
     * by {@code DyadicCountSketch}. For two {@code FrequencyTable} objects to be
     * mergeable, they must have the same size. To ensure that this is the case, an
     * existing {@code FrequencyTable} can be copied using {@code copy()} and reset
     * using {@code reset()}.
     *
     * @author Martin de Spirlet
     * @see #copy()
     * @see #merge(CountSketch)
     * @see #reset()
     * @see DyadicCountSketch
     * @see FrequencySummary
     * @see MultisetSummary
     * @see Summary
     */
    private static class FrequencyTable implements FrequencySummary<FrequencyTable> {

        private final int OFFSET;
        private final int SIZE;

        private int[] counters;

        /**
         * Constructs a copy of the given {@code FrequencyTable}.
         *
         * @param countSketch the {@code FrequencyTable} to copy
         */
        private FrequencyTable(FrequencyTable frequencyTable) {
            OFFSET = frequencyTable.OFFSET;
            SIZE = frequencyTable.SIZE;

            counters = Arrays.copyOf(frequencyTable.counters, SIZE);
        }

        /**
         * Constructs a new {@code FrequencyTable} with the given size.
         *
         * @param size the size of the new {@code FrequencyTable}
         */
        public FrequencyTable(int size) {
            SIZE = size;

            OFFSET = SIZE >> 1;

            reset();
        }

        /**
         * Returns the approximate frequency of the given item in the multiset from
         * which this {@code FrequencyTable} is built. Note that the number of bits
         * required to represent the two's complement of the item must be no more than
         * the number of bits required to represent a range of values equal to the size
         * of this {@code FrequencyTable}.
         *
         * @param item the item whose frequency to get
         * @return the approximate frequency of the given item in the multiset from
         *         which this {@code FrequencyTable} is built
         * @throws IndexOutOfBoundsException if the number of bits required to represent
         *                                   the two's complement of the item is greater
         *                                   than the number of bits required to
         *                                   represent a range of values equal to the
         *                                   size of this {@code FrequencyTable}
         */
        @Override
        public int getFrequency(int item) {
            return counters[item + OFFSET];
        }

        /**
         * Updates the representation of the given item in this {@code FrequencyTable}
         * by the corresponding weight. Note that the number of bits required to
         * represent the two's complement of the item must be no more than the number of
         * bits required to represent a range of values equal to the size of this
         * {@code FrequencyTable}.
         *
         * @param item   the item to update
         * @param weight the weight by which to update the item
         * @throws IndexOutOfBoundsException if the number of bits required to represent
         *                                   the two's complement of the item is greater
         *                                   than the number of bits required to
         *                                   represent a range of values equal to the
         *                                   size of this {@code FrequencyTable}
         */
        @Override
        public void update(int item, int weight) {
            counters[item + OFFSET] += weight;
        }

        /**
         * Returns a defensive copy of this {@code FrequencyTable}. This includes its
         * size and counter array.
         *
         * @return a defensive copy of this {@code FrequencyTable}
         */
        @Override
        public FrequencyTable copy() {
            return new FrequencyTable(this);
        }

        /**
         * Merges the given {@code FrequencyTable} into this {@code FrequencyTable}. For
         * two {@code FrequencyTable} objects to be merged, they must have the same
         * size. To ensure that this is the case, an existing {@code FrequencyTable} can
         * be copied using {@code copy()} and reset using {@code reset()}.
         *
         * @param other the {@code FrequencyTable} to merge into this
         *              {@code FrequencyTable}
         * @throws IndexOutOfBoundsException if the other {@code FrequencyTable} does
         *                                   not have the same size as this
         *                                   {@code FrequencyTable}
         * @see #copy()
         * @see #reset()
         */
        @Override
        public void merge(FrequencyTable other) {
            for (int i = 0; i < SIZE; i++) {
                counters[i] += other.counters[i];
            }
        }

        /**
         * Resets this {@code FrequencyTable} to its initial state. This sets its
         * counter array elements to zero.
         */
        @Override
        public void reset() {
            counters = new int[SIZE];
        }

        /**
         * Returns a {@code String} that represents this {@code FrequencyTable}.
         *
         * @return a {@code String} that represents this {@code FrequencyTable}
         */
        @Override
        public String toString() {
            return "FrequencyTable [SIZE=" + SIZE + ", OFFSET=" + OFFSET + ", counters=" + Arrays.toString(counters)
                    + "]";
        }

    }

    private static final int LEVELS = Integer.SIZE;

    private final int COLUMNS;
    private final int ROWS;
    private final int THRESHOLD_LEVEL;

    private FrequencySummary[] summaries;

    /**
     * Constructs a copy of the given {@code DyadicCountSketch}.
     *
     * @param dyadicCountSketch the {@code DyadicCountSketch} to copy
     */
    private DyadicCountSketch(DyadicCountSketch dyadicCountSketch) {
        COLUMNS = dyadicCountSketch.COLUMNS;
        ROWS = dyadicCountSketch.ROWS;
        THRESHOLD_LEVEL = dyadicCountSketch.THRESHOLD_LEVEL;

        summaries = Arrays.stream(dyadicCountSketch.summaries).map(Summary::copy).toArray(FrequencySummary[]::new);
    }

    /**
     * Constructs a new {@code DyadicCountSketch} whose constituent
     * {@code CountSketch} objects each have the given number of rows and number of
     * columns.
     *
     * @param rows    the number of rows of each constituent {@code CountSketch} of
     *                the new {@code DyadicCountSketch}
     * @param columns the number of columns of each constituent {@code CountSketch}
     *                of the new {@code DyadicCountSketch}
     * @see CountSketch
     */
    public DyadicCountSketch(int rows, int columns) {
        ROWS = rows;
        COLUMNS = columns;

        THRESHOLD_LEVEL = LEVELS - Integer.numberOfLeadingZeros(Integer.MAX_VALUE / (ROWS * COLUMNS)) + 1;

        reset();
    }

    /**
     * Returns an item that has approximately the given rank in the multiset from
     * which this {@code DyadicCountSketch} is built.
     *
     * @param rank the rank of the item to get
     * @return an item that has approximately the given rank in the multiset from
     *         which this {@code DyadicCountSketch} is built.
     */
    @Override
    public synchronized int getItem(int rank) {
        int item = -1;
        int cumulativeRank = 0;

        int i = LEVELS - 1;
        while (true) {
            int frequency = summaries[i].getFrequency(item);

            if ((cumulativeRank + frequency) < rank) {
                ++item;
                cumulativeRank += frequency;
            }

            if (--i < 0) {
                break;
            }

            item <<= 1;
        }

        return item;
    }

    /**
     * Returns the approximate rank of the given item in the multiset from which
     * this {@code DyadicCountSketch} is built.
     *
     * @param item the item whose rank to get
     * @return the approximate rank of the given item in the multiset from which
     *         this {@code DyadicCountSketch} is built.
     */
    @Override
    public synchronized int getRank(int item) {
        int rank = 0;

        for (int i = 0; i < LEVELS - 1; i++) {
            if ((item % 2) != 0) {
                rank += summaries[i].getFrequency(item - 1);
            }

            item >>= 1;
        }

        if (item == 0) {
            rank += summaries[LEVELS - 1].getFrequency(item - 1);
        }

        return rank;
    }

    /**
     * Updates the representation of the given item in this
     * {@code DyadicCountSketch} by the corresponding weight.
     *
     * @param item   the item to update
     * @param weight the weight by which to update the item
     */
    @Override
    public synchronized void update(int item, int weight) {
        for (int i = 0; i < LEVELS; i++) {
            summaries[i].update(item, weight);

            item >>= 1;
        }
    }

    /**
     * Returns a defensive copy of this {@code DyadicCountSketch}. This includes its
     * constituent {@code CountSketch} and {@code FrequencyTable} objects.
     *
     * @return a defensive copy of this {@code CountSketch}
     * @see FrequencyTable
     * @see CountSketch
     */
    @Override
    public synchronized DyadicCountSketch copy() {
        return new DyadicCountSketch(this);
    }

    /**
     * Merges the given {@code DyadicCountSketch} into this
     * {@code DyadicCountSketch}. For two {@code DyadicCountSketch} objects to be
     * merged, each corresponding pair of their constituent {@code CountSketch} and
     * {@code FrequencyTable} objects must be mergeable. To ensure that this is the
     * case, an existing {@code DyadicCountSketch} can be copied using
     * {@code copy()} and reset using {@code reset()}. Note that if the merge fails,
     * this {@code DyadicCountSketch} may not be in its original state.
     *
     * @param other the {@code DyadicCountSketch} to merge into this
     *              {@code CountSketch}
     * @throws IllegalArgumentException if the constituent {@code CountSketch} and
     *                                  {@code FrequencyTable} objects of the other
     *                                  {@code DyadicCountSketch} cannot be merged
     *                                  with those of this {@code DyadicCountSketch}
     * @see #copy()
     * @see #reset()
     * @see FrequencyTable
     * @see CountSketch
     */
    @Override
    public synchronized void merge(DyadicCountSketch other) {
        try {
            for (int i = 0; i < LEVELS; i++) {
                summaries[i].merge(other.summaries[i]);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Cannot merge DyadicCountSketches whose corresponding pairs of constituent CountSketches and FrequencyTables are not mergeable: "
                            + this + " and " + other,
                    e);
        }
    }

    /**
     * Resets this {@code DyadicCountSketch} to its initial state. This resets its
     * constituent {@code CountSketch} and {@code FrequencyTable} objects.
     *
     * @see FrequencyTable
     * @see CountSketch
     */
    @Override
    public synchronized void reset() {
        summaries = new FrequencySummary[LEVELS];

        for (int i = 0; i < THRESHOLD_LEVEL; i++) {
            summaries[i] = new CountSketch(ROWS, COLUMNS);
        }

        for (int i = THRESHOLD_LEVEL; i < LEVELS; i++) {
            summaries[i] = new FrequencyTable(1 << (LEVELS - i));
        }
    }

    /**
     * Returns a {@code String} that represents this {@code DyadicCountSketch}.
     *
     * @return a {@code String} that represents this {@code DyadicCountSketch}
     */
    @Override
    public synchronized String toString() {
        return "DyadicCountSketch [ROWS=" + ROWS + ", COLUMNS=" + COLUMNS + ", THRESHOLD_LEVEL=" + THRESHOLD_LEVEL
                + ", summaries=" + Arrays.toString(summaries) + "]";
    }

}
