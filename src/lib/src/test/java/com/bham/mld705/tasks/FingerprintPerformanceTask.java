package com.bham.mld705.tasks;

import com.bham.mld705.tests.FingerprintTest;

/**
 * @author Martin de Spirlet
 */
public final class FingerprintPerformanceTask {

    private FingerprintPerformanceTask() {
        throw new AssertionError();
    }

    public static void main(String[] args) {
        new FingerprintTest(args).runPerformanceTest();
    }

}
