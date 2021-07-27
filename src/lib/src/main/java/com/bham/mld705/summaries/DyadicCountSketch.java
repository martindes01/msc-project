package com.bham.mld705.summaries;

import java.util.Arrays;

public final class DyadicCountSketch {

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

}
