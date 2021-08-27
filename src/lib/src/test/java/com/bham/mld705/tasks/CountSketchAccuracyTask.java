package com.bham.mld705.tasks;

import com.bham.mld705.tests.CountSketchTest;

/**
 * @author Martin de Spirlet
 */
public final class CountSketchAccuracyTask {

    private CountSketchAccuracyTask() {
        throw new AssertionError();
    }

    public static void main(String[] args) {
        new CountSketchTest(args).runAccuracyTest();
    }

}
